# Task: HR Application Implementation

## Objective
Build a single page HR application that provides employee profile page management and absence request functionality with role-based access control.

## Todo Items
- [x] Create project structure with backend (SpringBoot) and frontend (Next.js) directories
- [x] Set up Docker Compose configuration for PostgreSQL, backend, and frontend
- [x] Design and create database schema for users, employee profiles, and absence requests
- [x] Initialize SpringBoot backend with basic structure and dependencies
- [x] Initialize Next.js frontend with Tailwind CSS
- [x] Create shared models/DTOs for user roles and employee profiles
- [x] Implement authentication service with role-based access
- [x] Implement profile service for CRUD operations
- [x] Implement absence request service
- [x] Create basic frontend components for employee profiles
- [ ] Add unit tests for backend services
- [ ] Create README for deployment instructions

## Implementation Notes
- Following CLAUDE.md guidelines for simple, incremental changes
- Role-based access: Manager/Owner (full access), Co-worker (non-sensitive data + feedback), Employee (absence requests)
- Technology stack: SpringBoot + Next.js + PostgreSQL + Docker
- AI feedback polishing using HuggingFace model for co-worker feedback

## Review
[To be filled as tasks are completed]