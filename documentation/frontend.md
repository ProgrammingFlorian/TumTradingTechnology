# Frontend

Gradle handles most tasks

For a fresh installation of the frontend dependencies (including node)
- Run Gradle task `npm > npmCleanInstall`

To run full project and automatically compile frontend:
- Run Gradle `build`

To update/install missing dependencies:
- Run Gradle task `npm > npmInstall`

Manually start frontend only (better for debugging):
- Make sure [Node.js](https://nodejs.org/en/download/) is installed on your machine
- Navigate to the frontend folder (`cd frontend`)
- Run the frontend: `npm start`

### Technologies used

Main technologies:
- React
- TypeScript
- Bootstrap

### Overall structure

Routing is done in [index.tsx](../frontend/src/index.tsx), where all links can be found
