# A Mortgage Calculator

A simple mortgage calculator application built with Java 8.

## Features

- Calculate monthly mortgage payments
- View total payment and interest over the life of the loan
- Display a yearly amortization schedule
- Input validation with helpful error messages
- User-friendly GUI with clean design
- Command-line interface option

## Requirements

- Java 8 or higher
- Maven 3.6 or higher

## Building the Application

To build the application, run:

`mvn clean package`

This will create two JAR files in the `target` directory:
- `a-mortgage-calculator-1.0-SNAPSHOT.jar` - Main JAR file
- `a-mortgage-calculator-1.0-SNAPSHOT-jar-with-dependencies.jar` - Executable JAR with dependencies

## Running the Application

### GUI Version

To run the GUI version, use:

`java -jar target/a-mortgage-calculator-1.0-SNAPSHOT-jar-with-dependencies.jar`

### Command-line Version

To run the command-line version, use:

`java -cp target/a-mortgage-calculator-1.0-SNAPSHOT-jar-with-dependencies.jar com.mortgagecalculator.cli.MortgageCalculator`

## Usage

1. Enter the loan amount in dollars (e.g., 300000)
2. Enter the annual interest rate as a percentage (e.g., 4.5 for 4.5%)
3. Enter the loan term in years (e.g., 30)
4. Click "Calculate" (GUI) or press Enter (CLI)
5. View the monthly payment, total payment, and total interest
6. Check the amortization schedule for a year-by-year breakdown

## Development

This project uses:
- Java 8 features (lambdas, streams, functional interfaces, Optional)
- Maven for dependency management and building
- Swing for the GUI

## Continuous Integration

This project includes a `.gitlab-ci.yml` file for GitLab CI/CD integration. When pushed to GitLab, it will:
1. Build the project
2. Package the application

## License

This project is licensed under the MIT License - see the LICENSE file for details.