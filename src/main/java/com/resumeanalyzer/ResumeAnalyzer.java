package com.resumeanalyzer;

import java.util.*;

public class ResumeAnalyzer {
    private static final Map<String, Set<String>> JOB_KEYWORDS = new HashMap<>();
    
    static {
        // Frontend Developer keywords
        Set<String> frontendKeywords = new HashSet<>(Arrays.asList(
            "html", "css", "javascript", "react", "angular", "vue", "typescript",
            "responsive design", "ui/ux", "bootstrap", "sass", "webpack", "npm",
            "git", "rest api", "ajax", "jquery", "redux", "next.js"
        ));
        JOB_KEYWORDS.put("Frontend Developer", frontendKeywords);

        // Backend Developer keywords
        Set<String> backendKeywords = new HashSet<>(Arrays.asList(
            "java", "python", "node.js", "spring", "django", "express",
            "sql", "nosql", "mongodb", "postgresql", "mysql", "rest api",
            "graphql", "microservices", "docker", "kubernetes", "aws", "azure"
        ));
        JOB_KEYWORDS.put("Backend Developer", backendKeywords);

        // Full Stack Developer keywords
        Set<String> fullStackKeywords = new HashSet<>();
        fullStackKeywords.addAll(frontendKeywords);
        fullStackKeywords.addAll(backendKeywords);
        fullStackKeywords.addAll(Arrays.asList(
            "full stack", "devops", "ci/cd", "testing", "agile",
            "system design", "api design", "web security", "oauth",
            "jwt", "redis", "elasticsearch"
        ));
        JOB_KEYWORDS.put("Full Stack Developer", fullStackKeywords);

        // Data Analyst keywords
        Set<String> dataAnalystKeywords = new HashSet<>(Arrays.asList(
            "python", "r", "sql", "excel", "tableau", "power bi",
            "data visualization", "statistics", "machine learning", "pandas",
            "numpy", "matplotlib", "seaborn", "data cleaning", "etl"
        ));
        JOB_KEYWORDS.put("Data Analyst", dataAnalystKeywords);

        // Machine Learning Engineer keywords
        Set<String> mlEngineerKeywords = new HashSet<>(Arrays.asList(
            "python", "tensorflow", "pytorch", "scikit-learn", "keras",
            "deep learning", "machine learning", "neural networks", "nlp",
            "computer vision", "data preprocessing", "feature engineering",
            "model deployment", "mlops", "jupyter", "pandas", "numpy",
            "data mining", "optimization", "regression", "classification"
        ));
        JOB_KEYWORDS.put("Machine Learning Engineer", mlEngineerKeywords);

        // DevOps Engineer keywords
        Set<String> devOpsKeywords = new HashSet<>(Arrays.asList(
            "docker", "kubernetes", "jenkins", "aws", "azure", "gcp",
            "terraform", "ansible", "ci/cd", "git", "linux", "shell scripting",
            "monitoring", "logging", "prometheus", "grafana", "nginx",
            "security", "automation", "configuration management", "cloud"
        ));
        JOB_KEYWORDS.put("DevOps Engineer", devOpsKeywords);

        // UI/UX Designer keywords
        Set<String> uiUxKeywords = new HashSet<>(Arrays.asList(
            "figma", "sketch", "adobe xd", "photoshop", "illustrator",
            "wireframing", "prototyping", "user research", "usability testing",
            "information architecture", "interaction design", "visual design",
            "responsive design", "accessibility", "typography", "color theory",
            "user flows", "design systems", "design thinking", "user-centered design"
        ));
        JOB_KEYWORDS.put("UI/UX Designer", uiUxKeywords);
    }

    public static AnalysisResult analyzeResume(String resumeText, String jobRole) {
        if (!JOB_KEYWORDS.containsKey(jobRole)) {
            throw new IllegalArgumentException("Unsupported job role: " + jobRole);
        }

        Set<String> keywords = JOB_KEYWORDS.get(jobRole);
        Set<String> foundKeywords = new HashSet<>();
        Set<String> missingKeywords = new HashSet<>(keywords);
        
        String lowerResumeText = resumeText.toLowerCase();
        
        // Check for keywords
        for (String keyword : keywords) {
            if (lowerResumeText.contains(keyword.toLowerCase())) {
                foundKeywords.add(keyword);
                missingKeywords.remove(keyword);
            }
        }

        // Calculate score
        double score = (double) foundKeywords.size() / keywords.size() * 100;
        
        // Generate suggestions
        List<String> suggestions = new ArrayList<>();
        
        // Suggest most important missing keywords (limit to top 5)
        if (!missingKeywords.isEmpty()) {
            List<String> topMissingKeywords = new ArrayList<>(missingKeywords);
            Collections.shuffle(topMissingKeywords); // Randomize to avoid same suggestions every time
            int suggestCount = Math.min(5, topMissingKeywords.size());
            suggestions.add("Consider adding these key skills: " + 
                String.join(", ", topMissingKeywords.subList(0, suggestCount)));
        }
        
        // Check for common sections
        if (!lowerResumeText.contains("education")) {
            suggestions.add("Add an Education section to highlight your academic background");
        }
        if (!lowerResumeText.contains("experience")) {
            suggestions.add("Add an Experience section to showcase your work history");
        }
        if (!lowerResumeText.contains("skills")) {
            suggestions.add("Add a Skills section to list your technical and soft skills");
        }
        if (!lowerResumeText.contains("project")) {
            suggestions.add("Add a Projects section to demonstrate practical experience");
        }

        // Add role-specific suggestions
        switch (jobRole) {
            case "Frontend Developer":
                if (!lowerResumeText.contains("portfolio")) {
                    suggestions.add("Consider adding a link to your portfolio website");
                }
                break;
            case "UI/UX Designer":
                if (!lowerResumeText.contains("portfolio") && !lowerResumeText.contains("behance") && 
                    !lowerResumeText.contains("dribbble")) {
                    suggestions.add("Add links to your design portfolio (Behance/Dribbble)");
                }
                break;
            case "Machine Learning Engineer":
                if (!lowerResumeText.contains("github") && !lowerResumeText.contains("kaggle")) {
                    suggestions.add("Include links to your GitHub projects or Kaggle competitions");
                }
                break;
            case "DevOps Engineer":
                if (!lowerResumeText.contains("certification")) {
                    suggestions.add("Consider adding relevant cloud certifications (AWS/Azure/GCP)");
                }
                break;
        }

        return new AnalysisResult(score, foundKeywords, missingKeywords, suggestions);
    }

    public static class AnalysisResult {
        private final double score;
        private final Set<String> foundKeywords;
        private final Set<String> missingKeywords;
        private final List<String> suggestions;

        public AnalysisResult(double score, Set<String> foundKeywords, 
                            Set<String> missingKeywords, List<String> suggestions) {
            this.score = score;
            this.foundKeywords = foundKeywords;
            this.missingKeywords = missingKeywords;
            this.suggestions = suggestions;
        }

        public double getScore() { return score; }
        public Set<String> getFoundKeywords() { return foundKeywords; }
        public Set<String> getMissingKeywords() { return missingKeywords; }
        public List<String> getSuggestions() { return suggestions; }
    }
} 