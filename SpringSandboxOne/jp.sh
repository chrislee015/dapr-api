
#!/bin/bash

# jp.sh - Script to manage JSON placeholder test containers
# Usage: 
#   ./jp.sh [number_of_containers]     - Create containers
#   ./jp.sh --stop-all                 - Stop and remove all j* containers
#   ./jp.sh --help                     - Show help
# Examples: 
#   ./jp.sh 5                          - Create 5 containers (j1-j5)
#   ./jp.sh --stop-all                 - Stop and remove all containers

# Function to show help
show_help() {
    echo "JSON Placeholder Container Manager"
    echo ""
    echo "Usage:"
    echo "  $0 [number_of_containers]     Create containers (default: 3)"
    echo "  $0 --stop-all                 Stop and remove all j* containers"
    echo "  $0 --help                     Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 5                          Create 5 containers (j1, j2, j3, j4, j5)"
    echo "  $0 --stop-all                 Stop and remove all existing j* containers"
    echo ""
    echo "Container Details:"
    echo "  - Base image: svenwal/jsonplaceholder"
    echo "  - Naming: j1, j2, j3, etc."
    echo "  - Ports: 3000, 3001, 3002, etc."
    echo "  - Endpoints: /posts, /users, /comments, etc."
}

# Function to stop and remove all j* containers
stop_all_containers() {
    echo "Stopping and removing all JSON placeholder containers..."
    
    # Find all containers with names starting with 'j' followed by numbers
    EXISTING_CONTAINERS=$(docker ps -a --filter "name=^j[0-9]+$" --format "{{.Names}}" | sort -V)
    
    if [ -z "$EXISTING_CONTAINERS" ]; then
        echo "No JSON placeholder containers found."
        return 0
    fi
    
    echo "Found containers: $EXISTING_CONTAINERS"
    echo ""
    
    # Stop running containers
    RUNNING_CONTAINERS=$(docker ps --filter "name=^j[0-9]+$" --format "{{.Names}}")
    if [ ! -z "$RUNNING_CONTAINERS" ]; then
        echo "Stopping running containers..."
        docker stop $RUNNING_CONTAINERS
        echo "✓ Stopped containers: $RUNNING_CONTAINERS"
    fi
    
    # Remove all containers
    echo "Removing containers..."
    docker rm $EXISTING_CONTAINERS
    echo "✓ Removed containers: $EXISTING_CONTAINERS"
    
    echo ""
    echo "All JSON placeholder containers have been stopped and removed."
}

# Function to create containers
create_containers() {
    local NUM_CONTAINERS=$1
    
    echo "Creating $NUM_CONTAINERS JSON placeholder containers..."
    
    # Base port number - each container will use sequential ports starting from 3000
    BASE_PORT=3001
    
    # Array to store container names for summary
    CONTAINER_NAMES=()
    FAILED_CONTAINERS=()
    
    # Create containers
    for ((i=1; i<=NUM_CONTAINERS; i++)); do
        CONTAINER_NAME="j$i"
        PORT=$((BASE_PORT + i - 1))
        
        echo "Creating container: $CONTAINER_NAME on port $PORT"
        
        # Check if container already exists
        if docker ps -a --filter "name=^${CONTAINER_NAME}$" --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
            echo "⚠ Container $CONTAINER_NAME already exists. Skipping..."
            echo ""
            continue
        fi
        
        # Run the container
        docker run -d -p $PORT:3000 --name $CONTAINER_NAME svenwal/jsonplaceholder
        
        # Check if container was created successfully
        if [ $? -eq 0 ]; then
            CONTAINER_NAMES+=($CONTAINER_NAME)
            echo "✓ Container $CONTAINER_NAME created successfully on port $PORT"
        else
            FAILED_CONTAINERS+=($CONTAINER_NAME)
            echo "✗ Failed to create container $CONTAINER_NAME"
        fi
        
        echo ""
    done
    
    # Show summary
    echo "========================================="
    echo "Container Creation Summary:"
    echo "========================================="
    echo "Successfully created: ${#CONTAINER_NAMES[@]}"
    echo "Failed: ${#FAILED_CONTAINERS[@]}"
    
    if [ ${#CONTAINER_NAMES[@]} -gt 0 ]; then
        echo ""
        echo "Active containers:"
        for ((i=0; i<${#CONTAINER_NAMES[@]}; i++)); do
            CONTAINER_NAME=${CONTAINER_NAMES[$i]}
            # Find the container's port by extracting the container number
            CONTAINER_NUM=${CONTAINER_NAME#j}
            PORT=$((BASE_PORT + CONTAINER_NUM - 1))
            echo "  - $CONTAINER_NAME: http://localhost:$PORT"
        done
        
        echo ""
        echo "Test endpoints example:"
        echo "  - http://localhost:$BASE_PORT/posts"
        echo "  - http://localhost:$BASE_PORT/users" 
        echo "  - http://localhost:$BASE_PORT/comments"
        echo ""
        echo "Management commands:"
        echo "  - Stop all: $0 --stop-all"
        echo "  - Manual stop: docker stop ${CONTAINER_NAMES[*]}"
        echo "  - Manual remove: docker rm ${CONTAINER_NAMES[*]}"
    fi
    
    if [ ${#FAILED_CONTAINERS[@]} -gt 0 ]; then
        echo ""
        echo "Failed containers: ${FAILED_CONTAINERS[*]}"
    fi
    
    echo "========================================="
}

# Main script logic
case "$1" in
    --help|-h)
        show_help
        exit 0
        ;;
    --stop-all)
        stop_all_containers
        exit 0
        ;;
    "")
        # No argument provided, use default
        NUM_CONTAINERS=3
        create_containers $NUM_CONTAINERS
        ;;
    *)
        # Check if argument is a positive integer
        if [[ "$1" =~ ^[1-9][0-9]*$ ]]; then
            NUM_CONTAINERS=$1
            create_containers $NUM_CONTAINERS
        else
            echo "Error: Invalid argument '$1'"
            echo ""
            show_help
            exit 1
        fi
        ;;
esac