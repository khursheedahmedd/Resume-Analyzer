# AI-Powered Resume Analyzer

A Java-based desktop application that helps students assess and improve their resumes by providing instant feedback based on job-specific keyword matching and ATS compatibility.

## Features

- Upload and analyze PDF or DOCX resume files
- Job role-specific keyword matching
- Score calculation based on keyword presence
- Suggestions for missing sections and keywords
- Clean and intuitive JavaFX GUI

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository:

```bash
git clone <repository-url>
cd resume-analyzer
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn javafx:run
```

## Usage

1. Launch the application
2. Click "Select Resume" to choose your PDF or DOCX resume file
3. Select your target job role from the dropdown menu
4. Click "Analyze Resume" to get feedback
5. Review the analysis results, including:
   - Overall score
   - Found keywords
   - Missing keywords
   - Suggestions for improvement

## Supported Job Roles

- Frontend Developer
- Backend Developer
- Data Analyst

## Technical Details

The application uses:

- JavaFX for the GUI
- Apache PDFBox for PDF parsing
- Apache POI for DOCX parsing
- Maven for dependency management

## Contributing

Feel free to submit issues and enhancement requests!
