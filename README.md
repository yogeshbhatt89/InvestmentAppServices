# Investment App Services

Backend services for the Investment Application, built with Spring Boot.

## 🚀 Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.6.3 or later
- Git

### Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yogeshbhatt89/InvestmentAppServices.git
   cd InvestmentAppServices
   ```

2. **Run the setup script**
   - On Windows: Double-click setup.bat or run .\setup.bat in the terminal
   - On Linux/macOS: Run chmod +x setup.sh then ./setup.sh

   This will:
   - Set up Git hooks for code formatting
   - Install Maven Wrapper if needed
   - Install all dependencies
   - Format the codebase

3. **Configure environment variables**
   - Copy `.env.example` to `.env` and update the values
   - Or set the required environment variables in your system

### Running the Application

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🛠 Development

### Code Formatting

Code is automatically formatted on commit using Spotless. The pre-commit hook ensures consistent code style.

To manually format code:
```bash
./mvnw spotless:apply
```

### Testing

Run all tests:
```bash
./mvnw test
```

### Git Hooks

Git hooks are stored in the `.githooks` directory. The setup script configures Git to use these hooks.

## 📝 License

This project is licensed under the MIT License - see the license file for details.
