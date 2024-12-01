# Master Thesis 2024

To run the demonstrator, the backend should be started through the `AdsPlatformApplication.java` file and the frontend through '''npm start''' in the frontend folder.

To run the LLM tests, go to the `llm-module` folder and inside each of the strategies folders the steps are numbered and should be run in a python environment.

Also, install Ollama in your machine and pull the desired models to run. Change the models' names in the `models` object in each step file if necessary to make sure the model is called in the tests.

## Python packages to have installed in environment:

- geojson
- openpyxl
- re
- datetime
- requests
- progressbar
- numpy
- json

# ADS-Platform

`ADS-Platform` is a monolithic software component to provide a set of services to Agricultural Data Space (ADS).

**!! ATTENTION: This is a prototype, use at your own risk! !!**

## Provided Services

The provided services currently are:

### Twin-Hub

The `twin-hub` hosts Digital Twins (e.g. Digital Field Twins) and provides access to them via Web-API.

## Operation

This project uses GitLab-CI to build a Docker Image.

On the Server, we operate Docker Containers based on that Image; and we configure the deployment specific settings using
environment variables.
