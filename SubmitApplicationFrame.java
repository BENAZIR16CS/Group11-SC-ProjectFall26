import javax.swing.*;                       // Provides GUI components like JFrame, JTextField, JButton, JComboBox
import java.awt.*;                          // Provides layouts, fonts, colors, and spacing
import java.awt.event.ActionListener;       // Handles button and dropdown events
import java.io.File;                        // Reads selected file name from JFileChooser
import java.time.LocalDate;                 // Sets system submission date automatically
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// SubmitApplicationFrame implements Use Case 5: "Submit Application / Apply for Scholarship"
// Self-contained single file: compiles and runs directly in VS Code with no conflicts
// with other files in your project (such as Theme.java).
public class SubmitApplicationFrame extends JFrame {

    // SMS Navy & Light Theme colors (matching the group's Student Dashboard screenshot)
    private static final Color NAVY = new Color(16, 42, 67);
    private static final Color NAVY_ACTIVE = new Color(28, 61, 94);
    private static final Color SIDEBAR_TEXT = new Color(214, 224, 235);
    private static final Color SIDEBAR_MUTED = new Color(176, 196, 214);
    private static final Color BG_LIGHT = new Color(244, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_GRAY = new Color(220, 227, 236);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color READONLY_BG = new Color(248, 250, 252);

    // Fonts
    private static final Font BRAND_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font SECTION_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font SIDEBAR_USER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);

    // Advertised scholarships and students who already hold an approved scholarship (BR-04)
    private List<ScholarshipOption> advertisedScholarships;
    private Set<String> approvedStudentIds;
    private int nextApplicationId = 5005;

    // Student & Application Form Inputs (UC-5)
    private JTextField regNoField;
    private JTextField studentNameField;
    private JTextField emailField;
    private JTextField departmentField;
    private JComboBox<String> programCombo;
    private JTextField cgpaField;

    // Scholarship Selection + Read-only Derived Fields
    private JComboBox<ScholarshipOption> scholarshipCombo;
    private JTextField categoryField;
    private JTextField offeredProgramField;
    private JTextField deadlineField;
    private JCheckBox eligibilityConfirmedCheck;

    // Document Upload Inputs (Marksheets/degree, ID card, Bills, Income slip - PDF/DOCX only)
    private JTextField marksheetPathField;
    private JTextField idCardPathField;
    private JTextField billsPathField;
    private JTextField incomeSlipPathField;
    private JTextArea statementArea;

    // Auto-generated Application Summary Fields (Appendix A)
    private JLabel systemInfoLabel;
    private JButton clearButton;
    private JButton submitButton;

    // Constructor: builds the 1000x700 SMS Submit Application screen
    public SubmitApplicationFrame() {

        setTitle("SMS - Submit Application");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        advertisedScholarships = createAdvertisedScholarships();
        approvedStudentIds = new HashSet<>();
        approvedStudentIds.add("S-2026-010"); // Sample student already holding an approved scholarship (BR-04)

        // -------------------- LEFT NAVY SIDEBAR (Student Portal) --------------------
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);

        JLabel brandLabel = new JLabel("SMS");
        brandLabel.setFont(BRAND_FONT);
        brandLabel.setForeground(Color.WHITE);
        sidebarTop.add(brandLabel);
        sidebarTop.add(Box.createVerticalStrut(22));

        JLabel roleTitleLabel = new JLabel("QAU Student");
        roleTitleLabel.setFont(SIDEBAR_USER_FONT);
        roleTitleLabel.setForeground(Color.WHITE);
        sidebarTop.add(roleTitleLabel);

        JLabel subRoleLabel = new JLabel("Student Portal");
        subRoleLabel.setFont(SMALL_FONT);
        subRoleLabel.setForeground(SIDEBAR_MUTED);
        sidebarTop.add(subRoleLabel);
        sidebarTop.add(Box.createVerticalStrut(14));

        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(40, 72, 104));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebarTop.add(divider);
        sidebarTop.add(Box.createVerticalStrut(16));

        // Student Sidebar Navigation Options
        sidebarTop.add(createSidebarButton("View Available Scholarships", false, e ->
            JOptionPane.showMessageDialog(this, "Opens View Available Scholarships screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Check Eligibility Criteria", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-4: Check Eligibility Criteria screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Submit Application", true, e -> {}));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Track Application", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-6: Track Application Status screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("View Feedback", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-12: View Feedback screen.")
        ));

        sidebar.add(sidebarTop, BorderLayout.NORTH);

        JButton logoutButton = createSidebarButton("Log Out", false, e -> {
            JOptionPane.showMessageDialog(this, "Logged out from Student Portal.");
            dispose();
        });
        sidebar.add(logoutButton, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // -------------------- RIGHT CONTENT AREA --------------------
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BG_LIGHT);
        contentContainer.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel pageTitle = new JLabel("Submit Scholarship Application");
        pageTitle.setFont(HEADER_FONT);
        pageTitle.setForeground(TEXT_DARK);
        titleGroup.add(pageTitle);

        JLabel pageSubtitle = new JLabel(
            "Complete student details, choose an advertised scholarship for your program, and attach PDF/DOCX documents."
        );
        pageSubtitle.setFont(SUBTITLE_FONT);
        pageSubtitle.setForeground(TEXT_MUTED);
        titleGroup.add(pageSubtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        systemInfoLabel = new JLabel("Next App ID: " + nextApplicationId + "  |  Date: " + today + "  |  Status: Pending");
        systemInfoLabel.setFont(SMALL_FONT);
        systemInfoLabel.setForeground(TEXT_MUTED);
        headerPanel.add(systemInfoLabel, BorderLayout.EAST);

        contentContainer.add(headerPanel, BorderLayout.NORTH);

        // Main Form Card inside JScrollPane
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Section 1: Student Information
        addSectionHeader(formCard, gbc, 0, "1. Student & Academic Information");

        regNoField = new JTextField(18);
        studentNameField = new JTextField(18);
        emailField = new JTextField(18);
        departmentField = new JTextField("Computer Science", 18);
        programCombo = new JComboBox<>(new String[]{"BS", "MS", "MPhil", "PhD"});
        cgpaField = new JTextField(18);

        addTwoColumnRow(formCard, gbc, 1, "Registration No *:", regNoField, "Student Name *:", studentNameField);
        addTwoColumnRow(formCard, gbc, 2, "University Email *:", emailField, "Department *:", departmentField);
        addTwoColumnRow(formCard, gbc, 3, "Enrolled Program *:", programCombo, "Current CGPA *:", cgpaField);

        // Section 2: Advertised Scholarship Selection
        addSectionHeader(formCard, gbc, 4, "2. Scholarship Selection (BR-01, BR-02, BR-05)");

        scholarshipCombo = new JComboBox<>(advertisedScholarships.toArray(new ScholarshipOption[0]));
        categoryField = createReadOnlyField();
        offeredProgramField = createReadOnlyField();
        deadlineField = createReadOnlyField();

        addTwoColumnRow(formCard, gbc, 5, "Scholarship *:", scholarshipCombo, "Category (BR-01):", categoryField);
        addTwoColumnRow(formCard, gbc, 6, "Offered Program:", offeredProgramField, "Deadline:", deadlineField);

        eligibilityConfirmedCheck = new JCheckBox(
            "I confirm I have checked eligibility criteria for the selected scholarship (FR-17)"
        );
        eligibilityConfirmedCheck.setOpaque(false);
        eligibilityConfirmedCheck.setFont(SMALL_FONT);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formCard.add(eligibilityConfirmedCheck, gbc);

        // Section 3: Supporting Documents Upload (PDF / DOCX only per FR-18)
        addSectionHeader(formCard, gbc, 8, "3. Upload Supporting Documents (PDF or DOCX only)");

        marksheetPathField = createReadOnlyField();
        idCardPathField = createReadOnlyField();
        billsPathField = createReadOnlyField();
        incomeSlipPathField = createReadOnlyField();

        addFileUploadRow(formCard, gbc, 9, "Marksheets / Degree *:", marksheetPathField);
        addFileUploadRow(formCard, gbc, 10, "Student ID Card *:", idCardPathField);
        addFileUploadRow(formCard, gbc, 11, "Utility Bills (Need based):", billsPathField);
        addFileUploadRow(formCard, gbc, 12, "Income Slip (Need based):", incomeSlipPathField);

        JLabel statementLabel = new JLabel("Applicant Statement:");
        statementLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(statementLabel, gbc);

        statementArea = new JTextArea(2, 36);
        statementArea.setLineWrap(true);
        statementArea.setWrapStyleWord(true);
        JScrollPane statementScroll = new JScrollPane(statementArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formCard.add(statementScroll, gbc);

        // Action Buttons (Cancel / Submit)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        buttonPanel.setOpaque(false);

        clearButton = new JButton("Cancel / Clear Form");
        clearButton.setFont(BUTTON_FONT);
        clearButton.setPreferredSize(new Dimension(160, 34));

        submitButton = new JButton("Submit Application");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(NAVY);
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(165, 34));

        buttonPanel.add(clearButton);
        buttonPanel.add(submitButton);

        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 4;
        formCard.add(buttonPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        contentContainer.add(formScroll, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);

        // -------------------- EVENT HANDLERS --------------------
        scholarshipCombo.addActionListener(e -> updateSelectedScholarshipFields());
        clearButton.addActionListener(e -> clearForm());
        submitButton.addActionListener(e -> submitApplication());

        updateSelectedScholarshipFields();
        setVisible(true);
    }

    // Allows running SubmitApplicationFrame.java directly in VS Code
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SubmitApplicationFrame());
    }

    private JButton createSidebarButton(String text, boolean active, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(SIDEBAR_FONT);
        btn.setForeground(active ? Color.WHITE : SIDEBAR_TEXT);
        btn.setBackground(active ? NAVY_ACTIVE : NAVY);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(active);
        btn.setOpaque(active);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(action);
        return btn;
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(18);
        field.setEditable(false);
        field.setBackground(READONLY_BG);
        return field;
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        JLabel label = new JLabel(title);
        label.setFont(SECTION_FONT);
        label.setForeground(NAVY);
        label.setBorder(BorderFactory.createEmptyBorder(row == 0 ? 0 : 6, 0, 2, 0));
        panel.add(label, gbc);
    }

    private void addTwoColumnRow(
        JPanel panel, GridBagConstraints gbc, int row,
        String label1, JComponent field1,
        String label2, JComponent field2
    ) {
        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel l1 = new JLabel(label1);
        l1.setFont(LABEL_FONT);
        panel.add(l1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        panel.add(field1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel l2 = new JLabel(label2);
        l2.setFont(LABEL_FONT);
        panel.add(l2, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        panel.add(field2, gbc);
    }

    private void addFileUploadRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField pathField) {
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(pathField, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JButton browseBtn = new JButton("Choose File (PDF/DOCX)");
        browseBtn.setFont(SMALL_FONT);
        browseBtn.addActionListener(e -> chooseDocumentFile(pathField));
        panel.add(browseBtn, gbc);
    }

    private void chooseDocumentFile(JTextField targetField) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            targetField.setText(file.getName());
        }
    }

    private void updateSelectedScholarshipFields() {
        ScholarshipOption selected = (ScholarshipOption) scholarshipCombo.getSelectedItem();
        if (selected == null) return;
        categoryField.setText(selected.getCategory());
        offeredProgramField.setText(selected.getProgram());
        deadlineField.setText(selected.getDeadline());
    }

    // UC-5 Main Success Scenario + Flows A & B + Business Rules BR-04 & BR-05
    private void submitApplication() {
        String regNo = regNoField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();
        String studentProgram = (String) programCombo.getSelectedItem();
        String cgpaText = cgpaField.getText().trim();
        ScholarshipOption scholarship = (ScholarshipOption) scholarshipCombo.getSelectedItem();

        String marksheet = marksheetPathField.getText().trim();
        String idCard = idCardPathField.getText().trim();
        String bills = billsPathField.getText().trim();
        String incomeSlip = incomeSlipPathField.getText().trim();

        // Flow A: Check missing mandatory fields or required documents
        List<String> missingFields = new ArrayList<>();
        if (regNo.isEmpty()) missingFields.add("Registration No");
        if (studentName.isEmpty()) missingFields.add("Student Name");
        if (email.isEmpty()) missingFields.add("University Email");
        if (department.isEmpty()) missingFields.add("Department");
        if (cgpaText.isEmpty()) missingFields.add("Current CGPA");
        if (!eligibilityConfirmedCheck.isSelected()) missingFields.add("Eligibility Check confirmation");
        if (marksheet.isEmpty()) missingFields.add("Marksheets / Degree file");
        if (idCard.isEmpty()) missingFields.add("Student ID Card file");
        if (scholarship != null && "Need based".equalsIgnoreCase(scholarship.getCategory())) {
            if (bills.isEmpty()) missingFields.add("Utility Bills file (required for Need based)");
            if (incomeSlip.isEmpty()) missingFields.add("Income Slip file (required for Need based)");
        }

        if (!missingFields.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in the missing required fields/documents:\n- " + String.join("\n- ", missingFields),
                "Missing Information (Flow A)",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Flow B: Validate email, CGPA range (0.00 - 4.00), and PDF/DOCX formats (FR-18)
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid email address. Please enter a valid university email.",
                "Invalid Information (Flow B)",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            double cgpa = Double.parseDouble(cgpaText);
            if (cgpa < 0.0 || cgpa > 4.0) {
                throw new NumberFormatException("Out of range");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid CGPA. Please enter a numeric value between 0.00 and 4.00.",
                "Invalid Information (Flow B)",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!isSupportedDocument(marksheet)
            || !isSupportedDocument(idCard)
            || (!bills.isEmpty() && !isSupportedDocument(bills))
            || (!incomeSlip.isEmpty() && !isSupportedDocument(incomeSlip))) {
            JOptionPane.showMessageDialog(
                this,
                "Unsupported document format. Only .pdf and .docx files are supported (FR-18).",
                "Invalid Document Format (Flow B)",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // BR-04 / Alternate 5.1: Student already has an approved scholarship
        if (approvedStudentIds.contains(regNo.toUpperCase())) {
            JOptionPane.showMessageDialog(
                this,
                "This student already has an approved scholarship application.\nNo other scholarship is allowed (BR-04 / FR-19).",
                "Application Not Allowed (BR-04)",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // BR-05 / Alternate 5.2: Student program must match scholarship offered program
        if (scholarship != null && !scholarship.getProgram().equalsIgnoreCase(studentProgram)) {
            JOptionPane.showMessageDialog(
                this,
                "Program mismatch: You selected program '" + studentProgram + "', but '"
                + scholarship.getName() + "' is offered for '" + scholarship.getProgram() + "' students only (BR-05 / FR-17).",
                "Program Mismatch (BR-05)",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Success: Record application with Pending status and auto-generated Application ID
        int assignedId = nextApplicationId++;
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        systemInfoLabel.setText("Next App ID: " + nextApplicationId + "  |  Date: " + today + "  |  Status: Pending");

        JOptionPane.showMessageDialog(
            this,
            "Application submitted successfully!\n"
            + "Application ID: " + assignedId + "\n"
            + "Submission Date: " + today + "\n"
            + "Status: Pending",
            "Application Submitted",
            JOptionPane.INFORMATION_MESSAGE
        );

        clearForm();
    }

    private boolean isSupportedDocument(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".docx");
    }

    // Alternate 5.3: Cancel / Clear resets form without saving
    private void clearForm() {
        regNoField.setText("");
        studentNameField.setText("");
        emailField.setText("");
        departmentField.setText("Computer Science");
        programCombo.setSelectedIndex(0);
        cgpaField.setText("");
        scholarshipCombo.setSelectedIndex(0);
        eligibilityConfirmedCheck.setSelected(false);
        marksheetPathField.setText("");
        idCardPathField.setText("");
        billsPathField.setText("");
        incomeSlipPathField.setText("");
        statementArea.setText("");
        updateSelectedScholarshipFields();
    }

    private List<ScholarshipOption> createAdvertisedScholarships() {
        List<ScholarshipOption> list = new ArrayList<>();
        list.add(new ScholarshipOption(1001, "Need Based Scholarship BS", "Need based", "BS", "30/10/2026"));
        list.add(new ScholarshipOption(1002, "Merit Based Scholarship BS", "Merit based", "BS", "05/11/2026"));
        list.add(new ScholarshipOption(1003, "Need Based Support Grant MS", "Need based", "MS", "12/11/2026"));
        list.add(new ScholarshipOption(1004, "Research Merit Fellowship PhD", "Merit based", "PhD", "20/11/2026"));
        return list;
    }

    // Embedded model class so SubmitApplicationFrame.java compiles with zero external files
    private static class ScholarshipOption {
        private int id;
        private String name;
        private String category;
        private String program;
        private String deadline;

        public ScholarshipOption(int id, String name, String category, String program, String deadline) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.program = program;
            this.deadline = deadline;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getProgram() { return program; }
        public String getDeadline() { return deadline; }

        @Override
        public String toString() {
            return name + " (" + category + " - " + program + ")";
        }
    }
}