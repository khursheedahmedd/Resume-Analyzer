# AI-Powered Resume Analyzer

A Java-based desktop application that helps students assess and improve their resumes by providing instant feedback based on job-specific keyword matching and ATS compatibility.

## Features

- Upload and analyze PDF or DOCX resume files
- Job role-specific keyword matching
- Score calculation based on keyword presence
- Suggestions for missing sections and keywords
- AI-powered detailed analysis using Groq LLM
- Clean and intuitive JavaFX GUI

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Groq API key (for AI analysis feature)

## Setup

1. Clone the repository:

```bash
git clone <repository-url>
cd resume-analyzer
```

2. Configure the Groq API key (choose one method):

   a. Using configuration file:

   ```bash
   cp src/main/resources/config.properties.template src/main/resources/config.properties
   ```

   Then edit `src/main/resources/config.properties` and replace `your_api_key_here` with your actual Groq API key.

   b. Using environment variable:

   ```bash
   export GROQ_API_KEY=your_api_key_here
   ```

   c. Using the application UI:

   - Launch the application
   - Click "Configure API Key"
   - Enter your API key in the dialog

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn javafx:run
```

## Usage

1. Launch the application
2. Click "Select Resume" to choose your PDF or DOCX resume file
3. Select your target job role from the dropdown menu
4. (Optional) Check "Include AI-Powered Analysis" for detailed AI feedback
5. Click "Analyze Resume" to get feedback
6. Review the analysis results, including:
   - Overall score
   - Found keywords
   - Missing keywords
   - Suggestions for improvement
   - AI-powered detailed analysis (if enabled)

## Supported Job Roles

- Frontend Developer
- Backend Developer
- Full Stack Developer
- Data Analyst
- Machine Learning Engineer
- DevOps Engineer
- UI/UX Designer

## Technical Details

The application uses:

- JavaFX for the GUI
- Apache PDFBox for PDF parsing
- Apache POI for DOCX parsing
- Groq LLM for AI-powered analysis
- Maven for dependency management

## Contributing

Feel free to submit issues and enhancement requests!
