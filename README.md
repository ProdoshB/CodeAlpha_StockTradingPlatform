# CodeAlpha_StockTradingPlatform

A simulated stock trading platform in Java, built for the **CodeAlpha Java Programming Internship** (Task 2).
All companies and prices are fictional/simulated - no real market data or money is involved.

## Features
- **Market data display** for 5 stocks with price and daily % change
- **Buy / sell** operations with validation (funds, shares owned, quantity)
- Starting balance of Rs.1,00,000; average cost tracking and per-stock profit/loss
- **Portfolio performance over time:** each "next day" moves prices randomly (+/-5%) and records the portfolio value; view it as a text chart
- Full **transaction history**
- **File I/O persistence:** portfolio, prices and history are saved to `portfolio.dat` on exit and reloaded on start
- OOP design: `Stock`, `Market`, `Holding`, `Transaction`, `Portfolio`, `Storage`, `TradingPlatform`

## Run
Requires JDK 17+.

```bash
javac -d out src/*.java
java -cp out TradingPlatform
```

Use option **0** to save and exit (so progress is stored).

## Project structure
```
src/Stock.java            - a listed stock and its price
src/Market.java           - simulated market, random daily price moves
src/Holding.java          - shares owned and average cost
src/Transaction.java      - a buy/sell record
src/Portfolio.java        - cash, holdings, transactions, value history
src/Storage.java          - save/load to file
src/TradingPlatform.java  - console menu
```

Author: Prodosh Bhattacharya
