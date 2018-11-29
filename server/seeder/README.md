# Seeder

Populate a NextClass schema with saved WebAdvisor data.

# Quickstart

Build the Docker image, and then point the seeder at your empty NextClass PostgreSQL instance.

```
docker build -t seeder .

# Show help for the seeder options:
docker run -it --rm --network=host seeder seed --help

# Example:
docker run -it --rm --network=host seeder seed --hostname=app.coursecollider.com
```
