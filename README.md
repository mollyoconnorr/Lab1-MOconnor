# JuiceBottler - Multi-Threaded Orange Juice Production Simulation

## Overview
The JuiceBottler project simulates a multi-threaded orange juice production process. Oranges are fetched, peeled, juiced, and bottled in parallel by multiple workers in two plants. The project demonstrates both data parallelization (multiple plants) and task parallelization (multiple workers per plant) through the use of Java threads and synchronized queues. The program outputs a summary of the process, including the number of oranges fetched, processed, bottled, and wasted.

## Goals of Project

- **Multiple Plants**: At least two plants must be running to simulate parallel processing.
- **Multiple Workers per Plant**: Each plant must have workers performing specific tasks such as fetching, peeling, juicing, and bottling oranges.
- **Threading**: There must be at least five threads running:
  - The **main thread** which starts the plants and prints the final summary.
  - **Two worker threads per plant**, each performing a specific task (fetching, peeling, juicing, bottling).
  
- **Data Parallelization**: Simulate multiple plants running in parallel, processing oranges.
- **Task Parallelization**: Each plant should operate with multiple workers concurrently.
  
- **Optional Extra Credit**: Use **ANT** for building and running the project.

## Project Structure

### Classes
1. **Plant**: Represents a single plant in the juice production process. The plant manages multiple workers (fetcher, peeler, juicer, and bottler) and coordinates their tasks.
2. **Worker**: Represents a single worker in the plant, responsible for processing a specific task such as fetching, peeling, juicing, or bottling oranges.
3. **Orange**: Represents an individual orange being processed in the plant.
4. **JuiceBottler (Main Class)**: The entry point of the program that initializes the plants and starts the simulation.

### Data Structures
- **LinkedBlockingQueue**: Found out about LinkedBlockingQueue at https://www.geeksforgeeks.org/linkedblockingqueue-class-in-java/ and I decided it would work well for my implementation. Used for task coordination and communication between different stages of the juice production process (e.g., ready-to-fetch, ready-to-peel, ready-to-juice, ready-to-bottle).

### Threads
- **Main thread**: Initializes the plants, starts the threads, and prints the final summary of the operations.
- **Plant threads**: Manage the operation of the plants, running the main loop of the orange processing tasks.
- **Worker threads**: Perform individual tasks (fetching, peeling, juicing, and bottling oranges).

## Usage

### Setup and Execution
1. Clone the repository to your local machine.
2. Compile the project:
   - Using **ANT**: Run the following command from the project directory:
     ```bash
     ant run
     ```
   - Using **IDE**: Import the project into your Java IDE and run the `JuiceBottler` class.

3. The simulation will run, and after the specified processing time (5 seconds), the program will stop and print a summary of the results.

### Expected Output
The program will print a summary of the juice production process, including the following:
- Total number of oranges provided by all plants.
- Total number of oranges fetched, processed, bottled, and wasted.

### Example Output:
```bash
Plant[1] Processing oranges
Plant[2] Processing oranges
.
.
.
.
Plant[1] Done Processing
Plant[2] Done Processing

======= Juice Plant Processing Summary =======
Total Oranges Provided: 500
Total Oranges Fetched: 480
Total Oranges Processed: 460
Total Bottles Created: 153
Total Oranges Wasted: 7
==============================================
