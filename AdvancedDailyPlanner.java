import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdvancedDailyPlanner {
    static List<Task> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String FILE_NAME = "tasks.json";
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Task class to store task details
    static class Task {
        String description;
        Priority priority;
        Category category;
        LocalDate dueDate;
        LocalDateTime createdAt;
        boolean completed;
        String notes;
        
        Task(String description, Priority priority, Category category, LocalDate dueDate, String notes) {
            this.description = description;
            this.priority = priority;
            this.category = category;
            this.dueDate = dueDate;
            this.notes = notes;
            this.createdAt = LocalDateTime.now();
            this.completed = false;
        }
        
        @Override
        public String toString() {
            String status = completed ? "✓" : "○";
            String prioritySymbol = getPrioritySymbol();
            String dueDateStr = dueDate != null ? " (Due: " + dueDate.format(DATE_FORMAT) + ")" : "";
            return String.format("%s [%s] %s - %s%s", status, prioritySymbol, category, description, dueDateStr);
        }
        
        private String getPrioritySymbol() {
            return switch (priority) {
                case HIGH -> "!!!";
                case MEDIUM -> "!!";
                case LOW -> "!";
            };
        }
        
        public String toJson() {
            return String.format(
                "{\"description\":\"%s\",\"priority\":\"%s\",\"category\":\"%s\",\"dueDate\":\"%s\",\"createdAt\":\"%s\",\"completed\":%b,\"notes\":\"%s\"}",
                description.replace("\"", "\\\""),
                priority,
                category,
                dueDate != null ? dueDate.format(DATE_FORMAT) : "",
                createdAt.format(DATETIME_FORMAT),
                completed,
                notes.replace("\"", "\\\"")
            );
        }
    }
    
    enum Priority {
        LOW, MEDIUM, HIGH
    }
    
    enum Category {
        WORK, PERSONAL, HEALTH, EDUCATION, SHOPPING, OTHER
    }
    
    public static void main(String[] args) {
        loadTasks();
        System.out.println("🌟 Welcome to Advanced Daily Planner!");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("Choose an option: ", 1, 11);
            
            switch (choice) {
                case 1 -> addTask();
                case 2 -> viewTasks();
                case 3 -> viewTasksByCategory();
                case 4 -> viewTasksByPriority();
                case 5 -> viewOverdueTasks();
                case 6 -> markTaskComplete();
                case 7 -> editTask();
                case 8 -> deleteTask();
                case 9 -> searchTasks();
                case 10 -> showStatistics();
                case 11 -> {
                    saveTasks();
                    System.out.println("✅ Tasks saved successfully. Goodbye!");
                    return;
                }
            }
        }
    }
    
    static void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📋 ADVANCED DAILY PLANNER");
        System.out.println("=".repeat(50));
        System.out.println("1.  ➕ Add Task");
        System.out.println("2.  📋 View All Tasks");
        System.out.println("3.  📂 View Tasks by Category");
        System.out.println("4.  🔥 View Tasks by Priority");
        System.out.println("5.  ⚠️  View Overdue Tasks");
        System.out.println("6.  ✅ Mark Task Complete");
        System.out.println("7.  ✏️  Edit Task");
        System.out.println("8.  🗑️  Delete Task");
        System.out.println("9.  🔍 Search Tasks");
        System.out.println("10. 📊 View Statistics");
        System.out.println("11. 💾 Save and Exit");
        System.out.println("=".repeat(50));
    }
    
    static void addTask() {
        System.out.println("\n➕ Adding New Task");
        System.out.println("-".repeat(20));
        
        System.out.print("Enter task description: ");
        String description = scanner.nextLine().trim();
        
        if (description.isEmpty()) {
            System.out.println("❌ Task description cannot be empty!");
            return;
        }
        
        Priority priority = selectPriority();
        Category category = selectCategory();
        LocalDate dueDate = selectDueDate();
        
        System.out.print("Enter notes (optional): ");
        String notes = scanner.nextLine().trim();
        
        Task task = new Task(description, priority, category, dueDate, notes);
        tasks.add(task);
        System.out.println("✅ Task added successfully!");
    }
    
    static Priority selectPriority() {
        System.out.println("\nSelect Priority:");
        System.out.println("1. Low (!)");
        System.out.println("2. Medium (!!)");
        System.out.println("3. High (!!!)");
        
        int choice = getIntInput("Priority: ", 1, 3);
        return Priority.values()[choice - 1];
    }
    
    static Category selectCategory() {
        System.out.println("\nSelect Category:");
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        
        int choice = getIntInput("Category: ", 1, categories.length);
        return categories[choice - 1];
    }
    
    static LocalDate selectDueDate() {
        System.out.print("Enter due date (YYYY-MM-DD) or press Enter for no due date: ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(input, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format. No due date set.");
            return null;
        }
    }
    
    static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        System.out.println("\n📋 All Tasks (" + tasks.size() + " total)");
        System.out.println("=".repeat(60));
        
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%2d. %s\n", i + 1, tasks.get(i));
            if (!tasks.get(i).notes.isEmpty()) {
                System.out.println("    📝 " + tasks.get(i).notes);
            }
        }
    }
    
    static void viewTasksByCategory() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        Map<Category, List<Task>> tasksByCategory = new HashMap<>();
        for (Task task : tasks) {
            tasksByCategory.computeIfAbsent(task.category, k -> new ArrayList<>()).add(task);
        }
        
        System.out.println("\n📂 Tasks by Category");
        System.out.println("=".repeat(40));
        
        for (Category category : Category.values()) {
            List<Task> categoryTasks = tasksByCategory.get(category);
            if (categoryTasks != null && !categoryTasks.isEmpty()) {
                System.out.println("\n" + category + " (" + categoryTasks.size() + " tasks):");
                for (int i = 0; i < categoryTasks.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + categoryTasks.get(i));
                }
            }
        }
    }
    
    static void viewTasksByPriority() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        System.out.println("\n🔥 Tasks by Priority");
        System.out.println("=".repeat(40));
        
        for (Priority priority : Priority.values()) {
            List<Task> priorityTasks = tasks.stream()
                .filter(t -> t.priority == priority && !t.completed)
                .toList();
            
            if (!priorityTasks.isEmpty()) {
                System.out.println("\n" + priority + " Priority (" + priorityTasks.size() + " tasks):");
                for (int i = 0; i < priorityTasks.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + priorityTasks.get(i));
                }
            }
        }
    }
    
    static void viewOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = tasks.stream()
            .filter(t -> !t.completed && t.dueDate != null && t.dueDate.isBefore(today))
            .toList();
        
        if (overdueTasks.isEmpty()) {
            System.out.println("🎉 No overdue tasks!");
            return;
        }
        
        System.out.println("\n⚠️ Overdue Tasks (" + overdueTasks.size() + " tasks)");
        System.out.println("=".repeat(40));
        
        for (int i = 0; i < overdueTasks.size(); i++) {
            System.out.println((i + 1) + ". " + overdueTasks.get(i));
        }
    }
    
    static void markTaskComplete() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        viewTasks();
        int index = getIntInput("Enter task number to mark complete: ", 1, tasks.size()) - 1;
        
        Task task = tasks.get(index);
        task.completed = !task.completed;
        
        String status = task.completed ? "completed" : "incomplete";
        System.out.println("✅ Task marked as " + status + "!");
    }
    
    static void editTask() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        viewTasks();
        int index = getIntInput("Enter task number to edit: ", 1, tasks.size()) - 1;
        Task task = tasks.get(index);
        
        System.out.println("\nEditing: " + task.description);
        System.out.println("1. Edit description");
        System.out.println("2. Edit priority");
        System.out.println("3. Edit category");
        System.out.println("4. Edit due date");
        System.out.println("5. Edit notes");
        
        int choice = getIntInput("What to edit: ", 1, 5);
        
        switch (choice) {
            case 1 -> {
                System.out.print("New description: ");
                task.description = scanner.nextLine().trim();
            }
            case 2 -> task.priority = selectPriority();
            case 3 -> task.category = selectCategory();
            case 4 -> task.dueDate = selectDueDate();
            case 5 -> {
                System.out.print("New notes: ");
                task.notes = scanner.nextLine().trim();
            }
        }
        
        System.out.println("✅ Task updated successfully!");
    }
    
    static void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        viewTasks();
        int index = getIntInput("Enter task number to delete: ", 1, tasks.size()) - 1;
        
        System.out.print("Are you sure you want to delete this task? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            tasks.remove(index);
            System.out.println("🗑️ Task deleted successfully!");
        } else {
            System.out.println("❌ Deletion cancelled.");
        }
    }
    
    static void searchTasks() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available.");
            return;
        }
        
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        List<Task> results = tasks.stream()
            .filter(t -> t.description.toLowerCase().contains(searchTerm) || 
                        t.notes.toLowerCase().contains(searchTerm))
            .toList();
        
        if (results.isEmpty()) {
            System.out.println("🔍 No tasks found matching '" + searchTerm + "'");
            return;
        }
        
        System.out.println("\n🔍 Search Results (" + results.size() + " found)");
        System.out.println("=".repeat(40));
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }
    }
    
    static void showStatistics() {
        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks available for statistics.");
            return;
        }
        
        long completedTasks = tasks.stream().filter(t -> t.completed).count();
        long pendingTasks = tasks.size() - completedTasks;
        long overdueTasks = tasks.stream()
            .filter(t -> !t.completed && t.dueDate != null && t.dueDate.isBefore(LocalDate.now()))
            .count();
        
        System.out.println("\n📊 Task Statistics");
        System.out.println("=".repeat(30));
        System.out.println("Total Tasks: " + tasks.size());
        System.out.println("Completed: " + completedTasks);
        System.out.println("Pending: " + pendingTasks);
        System.out.println("Overdue: " + overdueTasks);
        
        if (tasks.size() > 0) {
            double completionRate = (double) completedTasks / tasks.size() * 100;
            System.out.printf("Completion Rate: %.1f%%\n", completionRate);
        }
        
        // Category breakdown
        System.out.println("\nTasks by Category:");
        Map<Category, Long> categoryCount = tasks.stream()
            .collect(java.util.stream.Collectors.groupingBy(t -> t.category, java.util.stream.Collectors.counting()));
        
        for (Category category : Category.values()) {
            long count = categoryCount.getOrDefault(category, 0L);
            if (count > 0) {
                System.out.println("  " + category + ": " + count);
            }
        }
    }
    
    static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("❌ Please enter a number between " + min + " and " + max);
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Please enter a valid number");
                scanner.nextLine(); // clear invalid input
            }
        }
    }
    
    static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                writer.write("  " + tasks.get(i).toJson());
                if (i < tasks.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]");
        } catch (IOException e) {
            System.out.println("❌ Error saving tasks: " + e.getMessage());
        }
    }
    
    static void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            
            parseJsonTasks(json.toString());
        } catch (IOException e) {
            System.out.println("❌ Error loading tasks: " + e.getMessage());
        }
    }
    
    static void parseJsonTasks(String json) {
        // Simple JSON parsing for task objects
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return;
        
        json = json.substring(1, json.length() - 1); // Remove brackets
        String[] taskJsons = json.split("\\},\\s*\\{");
        
        for (String taskJson : taskJsons) {
            if (!taskJson.trim().isEmpty()) {
                taskJson = taskJson.trim();
                if (!taskJson.startsWith("{")) taskJson = "{" + taskJson;
                if (!taskJson.endsWith("}")) taskJson = taskJson + "}";
                
                try {
                    Task task = parseTask(taskJson);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error parsing task: " + e.getMessage());
                }
            }
        }
    }
    
    static Task parseTask(String json) {
        try {
            String description = extractJsonValue(json, "description");
            Priority priority = Priority.valueOf(extractJsonValue(json, "priority"));
            Category category = Category.valueOf(extractJsonValue(json, "category"));
            String dueDateStr = extractJsonValue(json, "dueDate");
            LocalDate dueDate = dueDateStr.isEmpty() ? null : LocalDate.parse(dueDateStr, DATE_FORMAT);
            String createdAtStr = extractJsonValue(json, "createdAt");
            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, DATETIME_FORMAT);
            boolean completed = Boolean.parseBoolean(extractJsonValue(json, "completed"));
            String notes = extractJsonValue(json, "notes");
            
            Task task = new Task(description, priority, category, dueDate, notes);
            task.createdAt = createdAt;
            task.completed = completed;
            
            return task;
        } catch (Exception e) {
            return null;
        }
    }
    
    static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"([^\"]*)\"|\"" + key + "\":([^,}]*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (m.find()) {
            String value = m.group(1) != null ? m.group(1) : m.group(2);
            return value != null ? value.replace("\\\"", "\"") : "";
        }
        return "";
    }
}