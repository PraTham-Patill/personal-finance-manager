const express = require('express');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const app = express();
const PORT = 3000;

// Secret key for JWT
const JWT_SECRET = 'your-secret-key';

// In-memory database for demo purposes
let users = [];
let transactions = [];
let budgets = [];

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Serve static files from the current directory
app.use(express.static('.'));

// Sample data for initial testing
function initializeSampleData() {
    // Sample transactions
    if (transactions.length === 0) {
        transactions = [
            {
                id: 1,
                type: 'income',
                amount: 3000,
                description: 'Salary',
                category: 'salary',
                date: '2023-10-01',
                notes: 'Monthly salary'
            },
            {
                id: 2,
                type: 'expense',
                amount: 500,
                description: 'Rent',
                category: 'utilities',
                date: '2023-10-05',
                notes: 'Monthly rent'
            },
            {
                id: 3,
                type: 'expense',
                amount: 200,
                description: 'Groceries',
                category: 'food',
                date: '2023-10-10',
                notes: 'Weekly groceries'
            },
            {
                id: 4,
                type: 'expense',
                amount: 100,
                description: 'Dinner',
                category: 'dining',
                date: '2023-10-15',
                notes: 'Dinner with friends'
            },
            {
                id: 5,
                type: 'income',
                amount: 500,
                description: 'Freelance',
                category: 'freelance',
                date: '2023-10-20',
                notes: 'Website design project'
            }
        ];
    }
    
    // Sample budgets
    if (budgets.length === 0) {
        budgets = [
            {
                id: 1,
                category: 'food',
                limit: 500,
                spent: 200,
                month: 'october'
            },
            {
                id: 2,
                category: 'utilities',
                limit: 1000,
                spent: 500,
                month: 'october'
            },
            {
                id: 3,
                category: 'transportation',
                limit: 300,
                spent: 150,
                month: 'october'
            },
            {
                id: 4,
                category: 'entertainment',
                limit: 200,
                spent: 180,
                month: 'october'
            },
            {
                id: 5,
                category: 'dining',
                limit: 300,
                spent: 100,
                month: 'october'
            }
        ];
    }
}

// API Routes
app.post('/api/auth/register', (req, res) => {
    const { name, email, password } = req.body;
    
    // Validate input
    if (!name || !email || !password) {
        return res.status(400).json({
            success: false,
            message: 'Please provide name, email, and password'
        });
    }
    
    // Check if email already exists
    const existingUser = users.find(user => user.email === email);
    if (existingUser) {
        return res.status(409).json({
            success: false,
            message: 'Email already registered'
        });
    }
    
    // Create new user
    const newUser = {
        id: users.length + 1,
        name,
        email,
        password // In a real app, this would be hashed
    };
    
    users.push(newUser);
    
    res.status(201).json({
        success: true,
        message: 'User registered successfully',
        data: {
            id: newUser.id,
            name: newUser.name,
            email: newUser.email
        }
    });
});

app.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;
    
    // Validate input
    if (!email || !password) {
        return res.status(400).json({
            success: false,
            message: 'Please provide email and password'
        });
    }
    
    // Find user
    const user = users.find(user => user.email === email && user.password === password);
    if (!user) {
        return res.status(401).json({
            success: false,
            message: 'Invalid email or password'
        });
    }
    
    // Generate JWT token
    const token = jwt.sign({ userId: user.id }, JWT_SECRET, { expiresIn: '24h' });
    
    res.json({
        success: true,
        message: 'Login successful',
        data: {
            userId: user.id,
            name: user.name,
            email: user.email,
            token
        }
    });
});

// Protected route example
app.get('/api/users/profile', authenticateToken, (req, res) => {
    const user = users.find(user => user.id === req.userId);
    
    if (!user) {
        return res.status(404).json({
            success: false,
            message: 'User not found'
        });
    }
    
    res.json({
        success: true,
        data: {
            id: user.id,
            name: user.name,
            email: user.email
        }
    });
});

// Middleware to authenticate JWT token
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    
    if (!token) {
        return res.status(401).json({
            success: false,
            message: 'Authentication token is required'
        });
    }
    
    jwt.verify(token, JWT_SECRET, (err, decoded) => {
        if (err) {
            return res.status(403).json({
                success: false,
                message: 'Invalid or expired token'
            });
        }
        
        req.userId = decoded.userId;
        next();
    });
}

// Initialize sample data
initializeSampleData();

// Transaction API Routes
app.get('/api/transactions', authenticateToken, (req, res) => {
    res.json({
        success: true,
        data: transactions
    });
});

app.get('/api/transactions/:id', authenticateToken, (req, res) => {
    const transactionId = parseInt(req.params.id);
    const transaction = transactions.find(t => t.id === transactionId);
    
    if (!transaction) {
        return res.status(404).json({
            success: false,
            message: 'Transaction not found'
        });
    }
    
    res.json({
        success: true,
        data: transaction
    });
});

app.post('/api/transactions', authenticateToken, (req, res) => {
    const { type, amount, description, category, date, notes } = req.body;
    
    // Validate input
    if (!type || !amount || !description || !category || !date) {
        return res.status(400).json({
            success: false,
            message: 'Please provide all required fields'
        });
    }
    
    // Create new transaction
    const newTransaction = {
        id: transactions.length > 0 ? Math.max(...transactions.map(t => t.id)) + 1 : 1,
        type,
        amount: parseFloat(amount),
        description,
        category,
        date,
        notes: notes || ''
    };
    
    transactions.push(newTransaction);
    
    // Update budget spent amount if it's an expense
    if (type === 'expense') {
        const budget = budgets.find(b => b.category === category);
        if (budget) {
            budget.spent += parseFloat(amount);
        }
    }
    
    res.status(201).json({
        success: true,
        message: 'Transaction created successfully',
        data: newTransaction
    });
});

app.put('/api/transactions/:id', authenticateToken, (req, res) => {
    const transactionId = parseInt(req.params.id);
    const { type, amount, description, category, date, notes } = req.body;
    
    // Validate input
    if (!type || !amount || !description || !category || !date) {
        return res.status(400).json({
            success: false,
            message: 'Please provide all required fields'
        });
    }
    
    // Find transaction
    const transactionIndex = transactions.findIndex(t => t.id === transactionId);
    
    if (transactionIndex === -1) {
        return res.status(404).json({
            success: false,
            message: 'Transaction not found'
        });
    }
    
    const oldTransaction = transactions[transactionIndex];
    
    // Update budget spent amount if category or amount changed
    if (oldTransaction.type === 'expense') {
        const oldBudget = budgets.find(b => b.category === oldTransaction.category);
        if (oldBudget) {
            oldBudget.spent -= oldTransaction.amount;
        }
    }
    
    if (type === 'expense') {
        const newBudget = budgets.find(b => b.category === category);
        if (newBudget) {
            newBudget.spent += parseFloat(amount);
        }
    }
    
    // Update transaction
    const updatedTransaction = {
        ...oldTransaction,
        type,
        amount: parseFloat(amount),
        description,
        category,
        date,
        notes: notes || ''
    };
    
    transactions[transactionIndex] = updatedTransaction;
    
    res.json({
        success: true,
        message: 'Transaction updated successfully',
        data: updatedTransaction
    });
});

app.delete('/api/transactions/:id', authenticateToken, (req, res) => {
    const transactionId = parseInt(req.params.id);
    
    // Find transaction
    const transactionIndex = transactions.findIndex(t => t.id === transactionId);
    
    if (transactionIndex === -1) {
        return res.status(404).json({
            success: false,
            message: 'Transaction not found'
        });
    }
    
    const transaction = transactions[transactionIndex];
    
    // Update budget spent amount if it's an expense
    if (transaction.type === 'expense') {
        const budget = budgets.find(b => b.category === transaction.category);
        if (budget) {
            budget.spent -= transaction.amount;
        }
    }
    
    // Remove transaction
    transactions.splice(transactionIndex, 1);
    
    res.json({
        success: true,
        message: 'Transaction deleted successfully'
    });
});

// Budget API Routes
app.get('/api/budgets', authenticateToken, (req, res) => {
    const { month } = req.query;
    
    if (!month) {
        return res.status(400).json({
            success: false,
            message: 'Please provide a month parameter'
        });
    }
    
    const monthBudgets = budgets.filter(b => b.month.toLowerCase() === month.toLowerCase());
    
    res.json({
        success: true,
        data: monthBudgets
    });
});

// Reports API Route
app.get('/api/reports', authenticateToken, (req, res) => {
    const { type, period } = req.query;
    
    if (!type || !period) {
        return res.status(400).json({
            success: false,
            message: 'Please provide type and period parameters'
        });
    }
    
    let reportData = {};
    
    // Generate report data based on type and period
    if (type === 'expense-category') {
        // Group expenses by category
        const expensesByCategory = {};
        
        transactions.forEach(transaction => {
            if (transaction.type === 'expense') {
                if (!expensesByCategory[transaction.category]) {
                    expensesByCategory[transaction.category] = 0;
                }
                expensesByCategory[transaction.category] += transaction.amount;
            }
        });
        
        reportData = {
            labels: Object.keys(expensesByCategory),
            values: Object.values(expensesByCategory)
        };
    } else if (type === 'expense-trend') {
        // Group expenses by month
        const expensesByMonth = {
            'Jan': 0, 'Feb': 0, 'Mar': 0, 'Apr': 0, 'May': 0, 'Jun': 0,
            'Jul': 0, 'Aug': 0, 'Sep': 0, 'Oct': 0, 'Nov': 0, 'Dec': 0
        };
        
        transactions.forEach(transaction => {
            if (transaction.type === 'expense') {
                const month = new Date(transaction.date).toLocaleString('en-US', { month: 'short' });
                expensesByMonth[month] += transaction.amount;
            }
        });
        
        reportData = {
            labels: Object.keys(expensesByMonth),
            values: Object.values(expensesByMonth)
        };
    } else if (type === 'income-expense') {
        // Group income and expenses by month
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        const incomeByMonth = { 'Jan': 0, 'Feb': 0, 'Mar': 0, 'Apr': 0, 'May': 0, 'Jun': 0, 'Jul': 0, 'Aug': 0, 'Sep': 0, 'Oct': 0, 'Nov': 0, 'Dec': 0 };
        const expensesByMonth = { 'Jan': 0, 'Feb': 0, 'Mar': 0, 'Apr': 0, 'May': 0, 'Jun': 0, 'Jul': 0, 'Aug': 0, 'Sep': 0, 'Oct': 0, 'Nov': 0, 'Dec': 0 };
        
        transactions.forEach(transaction => {
            const month = new Date(transaction.date).toLocaleString('en-US', { month: 'short' });
            
            if (transaction.type === 'income') {
                incomeByMonth[month] += transaction.amount;
            } else {
                expensesByMonth[month] += transaction.amount;
            }
        });
        
        reportData = {
            labels: months,
            incomeValues: months.map(month => incomeByMonth[month]),
            expenseValues: months.map(month => expensesByMonth[month])
        };
    }
    
    res.json({
        success: true,
        data: reportData
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});