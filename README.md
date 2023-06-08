# Cloud Task Scheduling Optimization

## Overview

This repository focuses on the implementation of Bee Colony Optimization (BCO) for task scheduling in cloud computing environments, using the CloudSim simulation framework. The primary aim is to minimize makespan and distribute tasks optimally across available data centers.

The code base also includes pre-implemented task scheduling algorithms: First-Come-First-Serve (FCFS), Round Robin (RR), Shortest Job First (SJF), and Particle Swarm Optimization (PSO). These algorithms were forked from [Michael Fahmy Repository](https://github.com/michaelfahmy/cloudsim-task-scheduling) and form the basis for comparison with BCO.

## Dependencies

The project requires the following dependencies:
- CloudSim
- JSwarmBCO package (already included in the source code)

## Setting Up

To use this project, you need to add CloudSim as a dependency in your IDE. You can do this by downloading the CloudSim jar file and adding it to your project structure.

## Usage

The main focus of this project is on the Bee Colony Optimization algorithm. To use it, initialize an instance of the `BCOScheduler` class and call the appropriate scheduling method.

The BCO implementation uses the JSwarmBCO package. The classes `SchedulerBee`, `SchedulerBeeUpdate`, and `SchedulerFitnessFunction` provide the BCO-specific behavior such as random search, neighborhood exploration, and fitness evaluation. These classes extend from the base classes provided by JSwarmBCO.

For the other algorithms (FCFS, RR, SJF, PSO), you can also initialize an instance of their corresponding class and call the appropriate scheduling method.
