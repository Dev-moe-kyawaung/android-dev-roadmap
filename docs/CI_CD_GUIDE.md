# 🚀 CI/CD Pipeline Guide

## Overview
This document describes the complete CI/CD pipeline for the Android Developer Roadmap app.

## 🔄 Workflows

### 1. Continuous Integration (ci.yml)
Runs on every push and PR:
- Environment setup
- Gradle build
- Unit tests (multiple Java versions)
- Instrumented tests (multiple API levels)
- Lint analysis
- Detekt code analysis
- ktlint formatting
- Dependency checks

### 2. Continuous Deployment (cd.yml)
Runs on main branch and tags:
- Release preparation
- APK/AAB build
- Play Store deployment
- Firebase deployment
- GitHub release creation
- Notifications

### 3. Testing (testing.yml)
Comprehensive test suite:
- Unit tests with coverage
- Instrumented tests matrix
- UI tests (Espresso)
- Performance tests
- Snapshot tests

### 4. Code Quality (code-quality.yml)
Quality assurance:
- SonarQube analysis
- Security scanning
- Dependency vulnerabilities
- Static analysis
- Code style checks

### 5. Performance Monitoring (performance-monitoring.yml)
Performance metrics:
- Build time analysis
- APK size analysis
- Memory profiling
- Startup time analysis

## 🔑 Required Secrets
Configure these in GitHub Settings > Secrets:

