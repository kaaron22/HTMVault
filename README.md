# HTM Vault

## Introduction

Hello and welcome to HTM (Healthcare Technology Management) Vault! I designed and developed this application for my capstone project while attending Nashville Software School's (NSS) nine-month Software Engineering [program](https://nashvillesoftwareschool.com/programs/software-engineering).

The SE program is based on Amazon Technical Academy's curriculum and is the result of collaboration between NSS and Amazon. It is focused on learning/applying computer science concepts and back-end technologies using Java and AWS, including object-oriented programming, algorithms and data structures, concurrent programming, numerous AWS services, etc.

This application was developed as a system for use by a healthcare technology management provider to maintain a location-specific inventory of medical devices, as well as the records of maintenance performed on each device. This allows providers to keep track of compliance-related information, such as routine maintenance due-dates, as well as a performance history of a given device, among other features.

## Capstone Project

This project challenged me to individually design and implement an application, including a problem statement, use cases / user stories, and architectural details to provide the desired functionality, such as API endpoints, DynamoDB tables with GSIs, AWS Lambda functions, and public/client-side models.

While the program, and primary focus of the application, was on back-end technologies, it additionally challenged me to become more familiar with front-end technology, without formal training, in a short period of time, namely HTML, CSS, and JavaScript.

Please see the [design document](resources/design-document.md) for details of the initial user stories, API endpoint designs, and DynamoDB table designs, among other details that informed end-goals, including both in-scope features to be implemented by program completion, and stretch-goal features to be implemented in the future.

The CloudFront website is available to peruse [here](https://d337ybw2cvpbqx.cloudfront.net/). It will require account sign up, which utilizes AWS Cognito, after which you can add, update, retrieve, and deactivate (soft delete) devices, as well as create, update, retrieve, and complete device-associated maintenance records, including initial acceptance testing, routine preventative maintenance, and repairs.

Additionally, modification of device information and completion of maintenance will automatically update compliance-related information, if applicable, including details such as last preventative maintenance completion date and next due date.