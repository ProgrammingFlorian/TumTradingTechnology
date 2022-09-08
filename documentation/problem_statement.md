## Problem Statement

#### 1. The Problem

A lot of people don’t want to risk real money on stocks, but still want to learn more about investing on the real market with real prices, but fictive money.

### 2. Scenarios

Bruno is a 20-year-old student living in Munich. He’s highly interested in investing on the stock market, but due to his financial situation, he doesn’t want to risk losing his real money. In order to improve his investing skills and gain more confidence, he creates an account on “wanna-be-neobrocker”. He enters the main page and sees the overall performance of his portfolio and receives the starting capital. He searches for stocks of his interest. He is convinced by the financial statistics of the company “BMW AG” and selects 10 as the amount to buy. He clicks “buy” and the stock is added to his dashboard and portfolio. He reviews the individual performance of his bought stocks. Bruno clicks on the BMW stock to see the graph that is shown in a popup window where can see more details regarding the company and stock statistics.

### 3. Requirements

The following functional requirements (FR) and nonfunctional requirements (NFR) must be
addressed in the project.

FR1: View Dashboard: The user can (when logged in) see his portfolio performance (credit and percentual increase/decrease), his best and worst performing stock and a graph of his overall performance.

FR2: See bought stocks: The user can see a list of all the stocks he bought (with their name and a short performance overview). When he clicks on one of the stocks a detailed view of the stock opens up (FR3).

FR3: Detail stock view: The user can have a look at more detailed information and statistics about his stocks and about stocks he browses (FR4) (should show: current price, graph of performance, buy and sell buttons (depending on the stock status) and more financial information).

FR4: Browse for new stocks: The user can browse for stocks on a list.

FR5: Buy or sell stocks: The user is able to buy new stocks at the current stock price, which adds the stock to his portfolio. He is also able to sell his bought stocks.

NFR1: Usability: The system should be intuitive to use, and the user interface should be
easy to understand. Simple interactions should be completed in less than three clicks.
Complex interactions should be completed in less than six clicks.

NFR2: Conformance to guidelines: The design of the system should conform to the typical
usability guidelines such as Nielsen’s usability heuristics.

NFR3: Server system: A server subsystem with a couple of services must be used in the system.

Additional constraints:

- The version control system must be git.
- Source Code Documentation must be in HTML format.
- The server system must use the Spring Boot framework.

### 4. Target Environment

The system should run on all desktop operating systems (Windows, macOS, Unix) as a browser-based application which communicates with the Spring Boot server application.

### 5. Deliverables

- Requirements Analysis Document (RAD)
- System Design Document (SDD)
- Source code under version control including source code documentation

### 6. Client Acceptance Criteria

The system must demonstrate at least the following functionalities:

1) You can create an account.
2) You can search for stocks.
3) You can buy/sell them.
4) It shows you a list of your owned stocks and the portfolio balance.

The application communicates with the server system and conforms to the usability requirements.