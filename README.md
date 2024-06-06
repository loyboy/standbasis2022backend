# Project Name

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## About the Project

This is the Standbasis backend repo that manages the curiculum delivery process management system.

## Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites

Ensure you have the following software installed:

- Java Development Kit (JDK) 11 or higher
- Apache Maven
- Git

### Installation

1. Clone the repository:

   ```
     git clone https://github.com/loyboy/standbasis2022backend.git
   ```
2. Navigate to the project directory:

    ```
      cd standbasis2022backend
    ```

3. Install the dependencies:
    ```
     mvn clean install
    ```

## Usage

### Running the Application

To run the Spring Boot application, use the following command:

   ```
   mvn spring-boot:run
   ```
The application should start, and you can access it at `http://localhost:8080` in your web browser.

### Building the Application

To build the application into a JAR file, run:

   ```
   mvn clean package
   ```

The JAR file will be located in the `target` directory. You can run it with:
  ```
   java -jar target/standbasis2022backend.jar
  ```
### Running Tests

To run the tests, use:

   ```
   mvn test
   ```

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

Emmanuel Ephraim - [@loyboytech](https://x.com/loyboytech) - onehubdigita@gmail.com

Project Link: [https://github.com/loyboy/standbasis2022backend](https://github.com/loyboy/standbasis2022backend)
