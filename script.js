document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    // Initialize the application
    initApp();
});

// API base URL - change this to your deployed backend URL when deploying
const API_BASE_URL = 'http://localhost:3000';

// Setup fetch interceptor to include authentication token in all API requests
function setupFetchInterceptor() {
    const originalFetch = window.fetch;
    
    window.fetch = function(url, options = {}) {
        // Get current user from localStorage
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        
        // If user is logged in and has a token, add it to the request headers
        if (currentUser && currentUser.token) {
            // Create headers object if it doesn't exist
            if (!options.headers) {
                options.headers = {};
            }
            
            // If headers is a Headers object, convert it to a plain object
            if (options.headers instanceof Headers) {
                const plainHeaders = {};
                for (const [key, value] of options.headers.entries()) {
                    plainHeaders[key] = value;
                }
                options.headers = plainHeaders;
            }
            
            // Add Authorization header with JWT token
            options.headers['Authorization'] = `Bearer ${currentUser.token}`;
        }
        
        // Call original fetch with modified options
        return originalFetch(url, options)
            .then(response => {
                // If response is 401 Unauthorized, clear user data and redirect to login
                if (response.status === 401) {
                    localStorage.removeItem('currentUser');
                    window.location.href = 'login.html';
                    return Promise.reject(new Error('Session expired. Please login again.'));
                }
                return response;
            });
    };
}

// Check if user is authenticated
function checkAuthentication() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    
    // If no user is logged in, redirect to login page
    if (!currentUser && !window.location.href.includes('login.html')) {
        window.location.href = 'login.html';
        return;
    }
    
    // Check if token exists
    if (currentUser && !currentUser.token && !window.location.href.includes('login.html')) {
        // Token missing, redirect to login
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
        return;
    }
    
    // Update UI with user info if logged in
    if (currentUser) {
        const userNameElement = document.querySelector('.user-name');
        if (userNameElement) {
            userNameElement.textContent = currentUser.name;
        }
    }
}

function initApp() {
    // Initialize navigation
    initNavigation();
    
    // Initialize charts
    initCharts();
    
    // Initialize transaction modal
    initTransactionModal();
    
    // Initialize event listeners
    initEventListeners();
    
    // Load and display data
    loadData();
}

// Navigation functionality
function initNavigation() {
    const navLinks = document.querySelectorAll('.sidebar-menu li a');
    const sections = document.querySelectorAll('.section, .dashboard');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all links
            navLinks.forEach(link => {
                link.parentElement.classList.remove('active');
            });
            
            // Add active class to clicked link
            this.parentElement.classList.add('active');
            
            // Hide all sections
            sections.forEach(section => {
                section.classList.add('hidden');
            });
            
            // Show the target section
            const targetId = this.getAttribute('href').substring(1);
            const targetSection = document.getElementById(targetId);
            if (targetSection) {
                targetSection.classList.remove('hidden');
                
                // Load section-specific data if needed
                if (targetId === 'reports') {
                    updateReportDisplay();
                } else if (targetId === 'budgets') {
                    const selectedMonth = document.getElementById('budget-month-select')?.value || 'october';
                    loadBudgetData(selectedMonth);
                } else if (targetId === 'goals') {
                    // Load goals data if needed
                }
            } else {
                console.error(`Section with ID ${targetId} not found`);
            }
        });
    });
}

// Charts initialization
function initCharts() {
    // Expense breakdown chart (pie chart)
    const expenseCtx = document.getElementById('expense-breakdown-chart');
    if (expenseCtx) {
        new Chart(expenseCtx, {
            type: 'doughnut',
            data: {
                labels: ['Food', 'Utilities', 'Rent', 'Transportation', 'Entertainment', 'Shopping', 'Health'],
                datasets: [{
                    data: [350, 250, 800, 180, 120, 290, 140],
                    backgroundColor: [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#C9CBCF'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right'
                    }
                }
            }
        });
    }
    
    // Income vs Expenses chart (bar chart)
    const incomeExpenseCtx = document.getElementById('income-expense-chart');
    if (incomeExpenseCtx) {
        new Chart(incomeExpenseCtx, {
            type: 'bar',
            data: {
                labels: ['May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct'],
                datasets: [
                    {
                        label: 'Income',
                        data: [4200, 4000, 4500, 4300, 4800, 4250],
                        backgroundColor: '#4BC0C0'
                    },
                    {
                        label: 'Expenses',
                        data: [3200, 3300, 3400, 3100, 3500, 2300],
                        backgroundColor: '#FF6384'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '$' + value;
                            }
                        }
                    }
                }
            }
        });
    }
    
    // Expense category chart in reports section
    const expenseCategoryCtx = document.getElementById('expense-category-chart');
    if (expenseCategoryCtx) {
        new Chart(expenseCategoryCtx, {
            type: 'pie',
            data: {
                labels: ['Food', 'Utilities', 'Rent', 'Transportation', 'Entertainment', 'Shopping', 'Health'],
                datasets: [{
                    data: [520, 250, 800, 180, 120, 290, 140],
                    backgroundColor: [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#C9CBCF'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }
    
    // Expense trend chart in reports section
    const expenseTrendCtx = document.getElementById('expense-trend-chart');
    if (expenseTrendCtx) {
        new Chart(expenseTrendCtx, {
            type: 'line',
            data: {
                labels: ['May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct'],
                datasets: [{
                    label: 'Monthly Expenses',
                    data: [2100, 2300, 2200, 2400, 2000, 2300],
                    borderColor: '#FF6384',
                    backgroundColor: 'rgba(255, 99, 132, 0.1)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '$' + value;
                            }
                        }
                    }
                }
            }
        });
    }
}

// Transaction modal functionality
function initTransactionModal() {
    const modal = document.getElementById('transaction-modal');
    const addTransactionBtn = document.querySelector('.add-transaction-btn');
    const closeModal = document.querySelector('.close-modal');
    const cancelBtn = document.querySelector('.cancel-btn');
    const transactionForm = document.getElementById('transaction-form');
    
    // Open modal
    if (addTransactionBtn) {
        addTransactionBtn.addEventListener('click', function() {
            modal.style.display = 'block';
            // Set default date to today
            document.getElementById('transaction-date').valueAsDate = new Date();
        });
    }
    
    // Close modal with X button
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            modal.style.display = 'none';
        });
    }
    
    // Close modal with Cancel button
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            modal.style.display = 'none';
        });
    }
    
    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    // Handle form submission
    if (transactionForm) {
        transactionForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            // Get form values
            const type = document.getElementById('transaction-type-select').value;
            const amount = document.getElementById('transaction-amount').value;
            const description = document.getElementById('transaction-description').value;
            const category = document.getElementById('transaction-category-select').value;
            const date = document.getElementById('transaction-date').value;
            const notes = document.getElementById('transaction-notes').value;
            
            // Create transaction object
            const transaction = {
                type: type,
                amount: parseFloat(amount),
                description: description,
                category: category,
                date: date,
                notes: notes
            };
            
            // Show loading state
            const submitBtn = this.querySelector('.save-btn');
            const originalBtnText = submitBtn.textContent;
            submitBtn.textContent = 'Saving...';
            submitBtn.disabled = true;
            
            try {
                // Save transaction
                await saveTransaction(transaction);
                
                // Close modal
                modal.style.display = 'none';
                
                // Reset form
                transactionForm.reset();
                
                // Refresh data display
                await loadData();
            } catch (error) {
                console.error('Error saving transaction:', error);
                showErrorMessage('Failed to save transaction. Please try again.');
            } finally {
                // Reset button state
                submitBtn.textContent = originalBtnText;
                submitBtn.disabled = false;
            }
        });
    }
}

// Initialize other event listeners
function initEventListeners() {
    // Edit and delete buttons for transactions
    document.addEventListener('click', async function(e) {
        if (e.target.closest('.edit-btn')) {
            const row = e.target.closest('tr');
            if (row) {
                const transactionId = row.dataset.id;
                await editTransaction(transactionId);
            }
        }
        
        if (e.target.closest('.delete-btn')) {
            const row = e.target.closest('tr');
            if (row) {
                const transactionId = row.dataset.id;
                
                if (confirm('Are you sure you want to delete this transaction?')) {
                    try {
                        // Show loading state
                        showLoadingState();
                        
                        // Delete transaction
                        await deleteTransaction(transactionId);
                        
                        // Refresh data display
                        await loadData();
                    } catch (error) {
                        console.error('Error deleting transaction:', error);
                        showErrorMessage('Failed to delete transaction. Please try again.');
                    } finally {
                        hideLoadingState();
                    }
                }
            }
        }
    });
    
    // Logout button
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            // Clear user data from localStorage
            localStorage.removeItem('currentUser');
            
            // Redirect to login page
            window.location.href = 'login.html';
        });
    }
    
    // Setup global fetch interceptor for authentication
    setupFetchInterceptor();
    
    // Budget category filter
    const budgetMonthSelect = document.getElementById('budget-month');
    if (budgetMonthSelect) {
        budgetMonthSelect.addEventListener('change', function() {
            // Update budget display based on selected month
            loadBudgetData(this.value);
        });
    }
    
    // Report type and period selectors
    const reportTypeSelect = document.getElementById('report-type-select');
    const reportPeriodSelect = document.getElementById('report-period-select');
    
    if (reportTypeSelect) {
        reportTypeSelect.addEventListener('change', function() {
            // Update report display based on selected type
            updateReportDisplay();
        });
    }
    
    if (reportPeriodSelect) {
        reportPeriodSelect.addEventListener('change', function() {
            // Update report display based on selected period
            updateReportDisplay();
        });
    }
}

// Data management functions
async function loadData() {
    showLoading('dashboard');
    showLoading('transactions');
    
    try {
        // Fetch transactions from API
        const response = await fetch(`${API_BASE_URL}/api/transactions`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }
        
        const data = await response.json();
        const transactions = data.data || [];
        
        // Update dashboard with transaction data
        updateDashboard(transactions);
        
        // Update transactions table
        updateTransactionsTable(transactions);
        
        // Load budget data
        const selectedMonth = document.getElementById('budget-month-select').value;
        loadBudgetData(selectedMonth);
        
        // Load reports if reports section is visible
        if (document.getElementById('reports-section').classList.contains('active')) {
            const reportType = document.getElementById('report-type-select').value;
            const reportPeriod = document.getElementById('report-period-select').value;
            loadReportData(reportType, reportPeriod);
        }
    } catch (error) {
        console.error('Error loading data:', error);
        showError('dashboard', 'Failed to load data. Please try again.');
        showError('transactions', 'Failed to load transactions. Please try again.');
    } finally {
        hideLoading('dashboard');
        hideLoading('transactions');
    }
}

// Helper functions for UI state
function showLoadingState() {
    // You can implement a loading spinner or message here
    console.log('Loading data...');
}

function hideLoadingState() {
    // Hide the loading spinner or message
    console.log('Data loaded.');
}

function showErrorMessage(message) {
    // Display error message to user
    console.error(message);
    // You can implement a toast or alert here
    alert(message);
}

// Load transactions from API
async function getTransactions() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/transactions`);
        if (!response.ok) {
            throw new Error('Failed to fetch transactions');
        }
        const data = await response.json();
        return data.data || [];
    } catch (error) {
        console.error('Error fetching transactions:', error);
        // Return empty array if API call fails
        return [];
    }
}

async function saveTransaction(transaction) {
    try {
        // Log the transaction data for debugging
        console.log('Saving transaction:', transaction);
        
        // Get the current user from localStorage
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        
        // Prepare headers with authentication token
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // Add authorization header if user is logged in
        if (currentUser && currentUser.token) {
            headers['Authorization'] = `Bearer ${currentUser.token}`;
        }
        
        const response = await fetch(`${API_BASE_URL}/api/transactions`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(transaction)
        });

        // Check if response is ok
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            console.error('Server response:', response.status, errorData);
            throw new Error(errorData.message || `Failed to save transaction: ${response.status}`);
        }

        const data = await response.json();
        console.log('Transaction saved successfully:', data);
        return data;
    } catch (error) {
        console.error('Error saving transaction:', error);
        throw error;
    }
}

async function deleteTransaction(transactionId) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/transactions/${transactionId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Failed to delete transaction');
        }
        
        return true;
    } catch (error) {
        console.error('Error deleting transaction:', error);
        throw error;
    }
}

// Fix editTransaction function to use API_BASE_URL instead of relative URL
async function editTransaction(transactionId) {
    try {
        // Show loading state
        showLoadingState();
        
        // Fetch the transaction from API
        const response = await fetch(`${API_BASE_URL}/api/transactions/${transactionId}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch transaction details');
        }
        
        const transaction = await response.json();
        
        if (transaction) {
            // Open the modal
            const modal = document.getElementById('transaction-modal');
            modal.style.display = 'block';
            
            // Fill the form with transaction data
            document.getElementById('transaction-type-select').value = transaction.type;
            document.getElementById('transaction-amount').value = transaction.amount;
            document.getElementById('transaction-description').value = transaction.description;
            document.getElementById('transaction-category-select').value = transaction.category;
            document.getElementById('transaction-date').value = transaction.date;
            document.getElementById('transaction-notes').value = transaction.notes || '';
            
            // Change form submission to update instead of create
            const transactionForm = document.getElementById('transaction-form');
            transactionForm.dataset.mode = 'edit';
            transactionForm.dataset.id = transactionId;
            
            // Change button text
            document.querySelector('.save-btn').textContent = 'Update Transaction';
            
            // Override form submission
            const originalSubmit = transactionForm.onsubmit;
            transactionForm.onsubmit = async function(e) {
                e.preventDefault();
                
                // Get form values
                const type = document.getElementById('transaction-type-select').value;
                const amount = document.getElementById('transaction-amount').value;
                const description = document.getElementById('transaction-description').value;
                const category = document.getElementById('transaction-category-select').value;
                const date = document.getElementById('transaction-date').value;
                const notes = document.getElementById('transaction-notes').value;
                
                // Show loading state
                const submitBtn = this.querySelector('.save-btn');
                const originalBtnText = submitBtn.textContent;
                submitBtn.textContent = 'Updating...';
                submitBtn.disabled = true;
                
                try {
                    // Update transaction
                    await updateTransaction(transactionId, {
                        type: type,
                        amount: parseFloat(amount),
                        description: description,
                        category: category,
                        date: date,
                        notes: notes
                    });
                    
                    // Close modal
                    modal.style.display = 'none';
                    
                    // Reset form
                    transactionForm.reset();
                    transactionForm.dataset.mode = 'create';
                    delete transactionForm.dataset.id;
                    document.querySelector('.save-btn').textContent = 'Save Transaction';
                    
                    // Restore original submit handler
                    transactionForm.onsubmit = originalSubmit;
                    
                    // Refresh data display
                    await loadData();
                } catch (error) {
                    console.error('Error updating transaction:', error);
                    showErrorMessage('Failed to update transaction. Please try again.');
                } finally {
                    // Reset button state
                    submitBtn.textContent = originalBtnText;
                    submitBtn.disabled = false;
                }
            };
        }
        
        // Hide loading state
        hideLoadingState();
    } catch (error) {
        console.error('Error fetching transaction details:', error);
        showErrorMessage('Failed to load transaction details. Please try again.');
        hideLoadingState();
    }
}

async function updateTransaction(transactionId, updatedData) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/transactions/${transactionId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        });
        
        if (!response.ok) {
            throw new Error('Failed to update transaction');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error updating transaction:', error);
        throw error;
    }
}

// UI update functions
function updateDashboard(transactions) {
    // Calculate totals
    let totalIncome = 0;
    let totalExpenses = 0;
    
    transactions.forEach(transaction => {
        if (transaction.type === 'income') {
            totalIncome += transaction.amount;
        } else {
            totalExpenses += transaction.amount;
        }
    });
    
    const balance = totalIncome - totalExpenses;
    
    // Update summary cards
    document.querySelector('.balance-amount').textContent = formatCurrency(balance);
    document.querySelector('.income-amount').textContent = formatCurrency(totalIncome);
    document.querySelector('.expenses-amount').textContent = formatCurrency(totalExpenses);
    
    // Update recent transactions table
    const recentTransactionsTable = document.querySelector('.recent-transactions .transactions-table tbody');
    if (recentTransactionsTable) {
        recentTransactionsTable.innerHTML = '';
        
        // Sort transactions by date (newest first) and take the first 5
        const recentTransactions = [...transactions]
            .sort((a, b) => new Date(b.date) - new Date(a.date))
            .slice(0, 5);
        
        recentTransactions.forEach(transaction => {
            const row = document.createElement('tr');
            row.dataset.id = transaction.id;
            
            const formattedDate = new Date(transaction.date).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric'
            });
            
            const amountClass = transaction.type === 'income' ? 'income' : 'expense';
            const amountPrefix = transaction.type === 'income' ? '+' : '-';
            
            row.innerHTML = `
                <td>${transaction.description}</td>
                <td>${getCategoryName(transaction.category)}</td>
                <td>${formattedDate}</td>
                <td class="${amountClass}">${amountPrefix}${formatCurrency(transaction.amount)}</td>
            `;
            
            recentTransactionsTable.appendChild(row);
        });
    }
}

function updateTransactionsTable(transactions) {
    const transactionsTable = document.querySelector('.transactions-table.full-table tbody');
    if (transactionsTable) {
        transactionsTable.innerHTML = '';
        
        // Sort transactions by date (newest first)
        const sortedTransactions = [...transactions].sort((a, b) => new Date(b.date) - new Date(a.date));
        
        sortedTransactions.forEach(transaction => {
            const row = document.createElement('tr');
            row.dataset.id = transaction.id;
            
            const formattedDate = new Date(transaction.date).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric'
            });
            
            const amountClass = transaction.type === 'income' ? 'income' : 'expense';
            const amountPrefix = transaction.type === 'income' ? '+' : '-';
            
            row.innerHTML = `
                <td>${transaction.description}</td>
                <td>${getCategoryName(transaction.category)}</td>
                <td>${formattedDate}</td>
                <td class="${amountClass}">${amountPrefix}${formatCurrency(transaction.amount)}</td>
                <td class="actions">
                    <button class="edit-btn"><i class="fas fa-edit"></i></button>
                    <button class="delete-btn"><i class="fas fa-trash"></i></button>
                </td>
            `;
            
            transactionsTable.appendChild(row);
        });
    }
}

async function loadBudgetData(month) {
    try {
        // Show loading state
        showLoadingState();
        
        // Fetch budget data from API
        const response = await fetch(`${API_BASE_URL}/api/budgets?month=${month}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch budget data');
        }
        
        const budgetData = await response.json();
        
        // Update budget display
        updateBudgetDisplay(budgetData);
        
        // Hide loading state
        hideLoadingState();
    } catch (error) {
        console.error('Error loading budget data:', error);
        showErrorMessage('Failed to load budget data. Please try again.');
        hideLoadingState();
    }
}

function updateBudgetDisplay(budgetData) {
    const budgetContainer = document.querySelector('.budget-cards');
    
    if (!budgetContainer || !budgetData || !budgetData.length) {
        // If no budget data, show a message
        if (budgetContainer) {
            budgetContainer.innerHTML = '<p class="no-data">No budget data available for this month.</p>';
        }
        return;
    }
    
    // Clear existing budget cards
    budgetContainer.innerHTML = '';
    
    // Create budget cards
    budgetData.forEach(budget => {
        const percentage = Math.min(100, Math.round((budget.spent / budget.limit) * 100));
        const cardClass = percentage >= 90 ? 'danger' : percentage >= 70 ? 'warning' : 'good';
        
        const card = document.createElement('div');
        card.className = `budget-card ${cardClass}`;
        
        card.innerHTML = `
            <div class="budget-info">
                <h3>${getCategoryName(budget.category)}</h3>
                <p>${formatCurrency(budget.spent)} / ${formatCurrency(budget.limit)}</p>
            </div>
            <div class="budget-progress">
                <div class="progress-bar">
                    <div class="progress" style="width: ${percentage}%"></div>
                </div>
                <span class="percentage">${percentage}%</span>
            </div>
        `;
        
        budgetContainer.appendChild(card);
    });
}

async function updateReportDisplay() {
    try {
        // Show loading state
        showLoadingState();
        
        const reportType = document.getElementById('report-type-select').value;
        const reportPeriod = document.getElementById('report-period-select').value;
        
        // Fetch report data from API
        const response = await fetch(`${API_BASE_URL}/api/reports?type=${reportType}&period=${reportPeriod}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch report data');
        }
        
        const reportData = await response.json();
        
        // Update report charts and displays based on the data
        updateReportCharts(reportData, reportType);
        
        // Hide loading state
        hideLoadingState();
    } catch (error) {
        console.error('Error loading report data:', error);
        showErrorMessage('Failed to load report data. Please try again.');
        hideLoadingState();
    }
}

function updateReportCharts(reportData, reportType) {
    // Update charts based on report type
    if (reportType === 'expense-category') {
        updateExpenseCategoryChart(reportData);
    } else if (reportType === 'expense-trend') {
        updateExpenseTrendChart(reportData);
    } else if (reportType === 'income-expense') {
        updateIncomeExpenseChart(reportData);
    }
}

function updateExpenseCategoryChart(data) {
    const ctx = document.getElementById('expense-category-chart');
    if (!ctx) return;
    
    // If chart already exists, destroy it
    if (window.expenseCategoryChart) {
        window.expenseCategoryChart.destroy();
    }
    
    // Create new chart
    window.expenseCategoryChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: data.labels,
            datasets: [{
                data: data.values,
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#C9CBCF'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

function updateExpenseTrendChart(data) {
    const ctx = document.getElementById('expense-trend-chart');
    if (!ctx) return;
    
    // If chart already exists, destroy it
    if (window.expenseTrendChart) {
        window.expenseTrendChart.destroy();
    }
    
    // Create new chart
    window.expenseTrendChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Monthly Expenses',
                data: data.values,
                borderColor: '#FF6384',
                backgroundColor: 'rgba(255, 99, 132, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value;
                        }
                    }
                }
            }
        }
    });
}

function updateIncomeExpenseChart(data) {
    const ctx = document.getElementById('income-expense-chart');
    if (!ctx) return;
    
    // If chart already exists, destroy it
    if (window.incomeExpenseChart) {
        window.incomeExpenseChart.destroy();
    }
    
    // Create new chart
    window.incomeExpenseChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: 'Income',
                    data: data.incomeValues,
                    backgroundColor: '#4BC0C0'
                },
                {
                    label: 'Expenses',
                    data: data.expenseValues,
                    backgroundColor: '#FF6384'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value;
                        }
                    }
                }
            }
        }
    });
}

// Helper functions
function formatCurrency(amount) {
    return amount.toLocaleString('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2
    }).replace('$', '');
}

function getCategoryName(categoryValue) {
    const categories = {
        'food': 'Food & Groceries',
        'utilities': 'Utilities',
        'transportation': 'Transportation',
        'entertainment': 'Entertainment',
        'shopping': 'Shopping',
        'health': 'Health',
        'education': 'Education',
        'salary': 'Salary',
        'freelance': 'Freelance',
        'dining': 'Dining'
    };
    
    return categories[categoryValue] || categoryValue;
}
// Add loadReportData function that was missing
async function loadReportData(reportType, reportPeriod) {
    try {
        // Show loading state
        showLoadingState();
        
        // Fetch report data from API
        const response = await fetch(`${API_BASE_URL}/api/reports?type=${reportType}&period=${reportPeriod}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch report data');
        }
        
        const reportData = await response.json();
        
        // Update report charts and displays based on the data
        updateReportCharts(reportData, reportType);
        
        // Hide loading state
        hideLoadingState();
    } catch (error) {
        console.error('Error loading report data:', error);
        showErrorMessage('Failed to load report data. Please try again.');
        hideLoadingState();
    }
}
