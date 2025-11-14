[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](#tech-stack)
# E-Voting System

A secure, reliable, and educational desktop voting application implemented in Java with a Swing GUI. This project demonstrates Object-Oriented Programming (OOP) principles and core custom data-structure implementations while providing a simple, auditable digital election workflow.

## Abstract

The E-Voting System is a software application designed to automate and secure 
the voting process using fundamental concepts of Object-Oriented 
Programming (OOP) and core Data Structures. The system provides a digital 
platform where voters can cast their votes, administrators can manage elections, 
and results are generated efficiently.
## Key features
- Role-based login for Voters and Admins
- Voter portal: view candidates and cast vote securely
- Admin dashboard: add/remove voters & candidates, view results and audit logs
- Live results and bar-chart visualization
- Duplicate-vote prevention and input validation
- Audit trail of system activities (logs with timestamps)

## Core data structures & algorithms
- VoterList — custom Singly Linked List (stores Voter records; linear search)
- VotingQueue — array‑based circular queue (buffers vote requests)
- CandidateList — dynamic resizable array (stores candidates; linear search, sort before display)
- AuditLog — stack semantics for recent actions (LIFO)
- Algorithms: linear search for lookup and validation, bubble sort (teaching purpose) to order results


