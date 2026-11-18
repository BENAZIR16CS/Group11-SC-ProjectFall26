import javax.swing.*;                       // Provides GUI components like JFrame, JTable, JLabel, JButton
import javax.swing.table.DefaultTableModel; // Manages rows inside the submitted applications table
import java.awt.*;                          // Provides layouts, fonts, colors, and spacing
import java.awt.event.ActionListener;       // Handles button click events
import java.util.ArrayList;                 // Stores submitted student applications
import java.util.List;

// VerifyApplicationFrame implements Use Case 8: "Verifying Student Application"
// Self-contained single file: compiles and runs directly in VS Code with no conflicts
// with other files in your project (such as Theme.java).
public class VerifyApplicationFrame extends JFrame {

    // SMS Navy & Light Theme colors (matching the group's dashboard screenshot)
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

    // Submitted student applications loaded from the system
    private List<ApplicationRecord> applications;
    private int selectedIndex = -1;

    // Applications list table (UC-8 Step 1 & 2)
    private JTable applicationTable;
    private DefaultTableModel tableModel;

    // Read-only fields displaying the selected student's submitted details (UC-8 Step 3)
    private JTextField appIdField;
    private JTextField regNoField;
    private JTextField studentNameField;
    private JTextField emailField;
    private JTextField departmentField;
    private JTextField programField;
    private JTextField scholarshipField;
    private JTextField categoryField;
    private JTextField submissionDateField;
    private JTextField cgpaField;
    private JTextField statusField;

    // Read-only display of uploaded files from the student
    private JTextArea documentsArea;

    // Committee Member inputs: verification checklist + remarks + decision buttons
    private JCheckBox documentsVerifiedCheck;
    private JCheckBox eligibilityVerifiedCheck;
    private JCheckBox simulateErrorCheck;
    private JTextArea remarksArea;

    private JButton openAppsButton;
    private JButton verifyButton;
    private JButton missingDocButton;
    private JButton clarifyButton;

    // Constructor: builds the 1000x700 SMS Verifying Applications screen
    public VerifyApplicationFrame() {

        setTitle("SMS - Verifying Applications"); // Window title matching group's SMS theme
        setSize(1000, 700); // Window dimensions matching teacher's sample code
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes program when window closes
        setLocationRelativeTo(null); // Centers window on screen
        setLayout(new BorderLayout()); // WEST: Sidebar, CENTER: Verification Review & Decision

        applications = createSampleApplications();

        // -------------------- LEFT NAVY SIDEBAR --------------------
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

        JLabel roleTitleLabel = new JLabel("Scholarship Committee");
        roleTitleLabel.setFont(SIDEBAR_USER_FONT);
        roleTitleLabel.setForeground(Color.WHITE);
        sidebarTop.add(roleTitleLabel);

        JLabel subRoleLabel = new JLabel("Committee Portal");
        subRoleLabel.setFont(SMALL_FONT);
        subRoleLabel.setForeground(SIDEBAR_MUTED);
        sidebarTop.add(subRoleLabel);
        sidebarTop.add(Box.createVerticalStrut(14));

        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(40, 72, 104));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebarTop.add(divider);
        sidebarTop.add(Box.createVerticalStrut(16));

        // Requested Committee Sidebar Options
        sidebarTop.add(createSidebarButton("Manage Scholarship", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-2: Manage Scholarship Information screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Advertisement", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-3: Advertise Scholarship screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Verifying Applications", true, e -> {}));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Schedule Interview", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-7: Schedule Interviews screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Conduct Interview", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-9: Conduct Online Interview screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Feedback", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-11: Generate Feedback screen.")
        ));

        sidebar.add(sidebarTop, BorderLayout.NORTH);

        JButton logoutButton = createSidebarButton("Log Out", false, e -> {
            JOptionPane.showMessageDialog(this, "Logged out from Scholarship Committee Portal.");
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

        JLabel pageTitle = new JLabel("Verifying Student Applications");
        pageTitle.setFont(HEADER_FONT);
        pageTitle.setForeground(TEXT_DARK);
        titleGroup.add(pageTitle);

        JLabel pageSubtitle = new JLabel(
            "Click Open Applications, select a student record to review details and documents, then submit your decision."
        );
        pageSubtitle.setFont(SUBTITLE_FONT);
        pageSubtitle.setForeground(TEXT_MUTED);
        titleGroup.add(pageSubtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topControls.setOpaque(false);

        simulateErrorCheck = new JCheckBox("Simulate Load Error");
        simulateErrorCheck.setOpaque(false);
        simulateErrorCheck.setFont(SMALL_FONT);

        openAppsButton = new JButton("Open Applications");
        openAppsButton.setFont(BUTTON_FONT);
        openAppsButton.setBackground(NAVY);
        openAppsButton.setForeground(Color.WHITE);
        openAppsButton.setOpaque(true);
        openAppsButton.setFocusPainted(false);
        openAppsButton.setPreferredSize(new Dimension(155, 32));

        topControls.add(simulateErrorCheck);
        topControls.add(openAppsButton);
        headerPanel.add(topControls, BorderLayout.EAST);

        contentContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Body: Top Table + Bottom Review & Decision Panel
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 12));
        bodyPanel.setOpaque(false);

        // 1) Submitted Applications Table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel tableTitle = new JLabel("Submitted Student Applications");
        tableTitle.setFont(SECTION_FONT);
        tableTitle.setForeground(TEXT_DARK);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"App ID", "Student Name", "Reg No", "Program", "Category", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationTable = new JTable(tableModel);
        applicationTable.setRowHeight(24);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(applicationTable);
        tableScroll.setPreferredSize(new Dimension(0, 125));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        bodyPanel.add(tableCard, BorderLayout.NORTH);

        // 2) Student Review (Read-Only) + Committee Verification Input Card
        JPanel reviewCard = new JPanel(new GridBagLayout());
        reviewCard.setBackground(CARD_BG);
        reviewCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Read-only fields (student submitted data)
        appIdField = createReadOnlyField();
        regNoField = createReadOnlyField();
        studentNameField = createReadOnlyField();
        emailField = createReadOnlyField();
        departmentField = createReadOnlyField();
        programField = createReadOnlyField();
        scholarshipField = createReadOnlyField();
        categoryField = createReadOnlyField();
        submissionDateField = createReadOnlyField();
        cgpaField = createReadOnlyField();
        statusField = createReadOnlyField();

        addTwoColumnRow(reviewCard, gbc, 0, "Application ID:", appIdField, "Registration No:", regNoField);
        addTwoColumnRow(reviewCard, gbc, 1, "Student Name:", studentNameField, "Student Email:", emailField);
        addTwoColumnRow(reviewCard, gbc, 2, "Department:", departmentField, "Program:", programField);
        addTwoColumnRow(reviewCard, gbc, 3, "Scholarship:", scholarshipField, "Category:", categoryField);
        addTwoColumnRow(reviewCard, gbc, 4, "Submission Date:", submissionDateField, "CGPA:", cgpaField);
        addTwoColumnRow(reviewCard, gbc, 5, "Current Status:", statusField, "", new JLabel(""));

        // Uploaded Documents (Read-Only review area)
        JLabel docsLabel = new JLabel("Submitted Documents:");
        docsLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        reviewCard.add(docsLabel, gbc);

        documentsArea = new JTextArea(4, 36);
        documentsArea.setEditable(false);
        documentsArea.setBackground(READONLY_BG);
        documentsArea.setLineWrap(true);
        documentsArea.setWrapStyleWord(true);
        JScrollPane docsScroll = new JScrollPane(documentsArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        reviewCard.add(docsScroll, gbc);

        // Committee Verification Checkboxes (Input from Committee Member)
        JLabel verifyChecksLabel = new JLabel("Verification Checks:");
        verifyChecksLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        reviewCard.add(verifyChecksLabel, gbc);

        documentsVerifiedCheck = new JCheckBox("Uploaded documents are valid & complete");
        eligibilityVerifiedCheck = new JCheckBox("Eligibility & program match confirmed (BR-04 / BR-05)");
        documentsVerifiedCheck.setOpaque(false);
        eligibilityVerifiedCheck.setOpaque(false);
        documentsVerifiedCheck.setFont(SMALL_FONT);
        eligibilityVerifiedCheck.setFont(SMALL_FONT);

        JPanel checksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        checksPanel.setOpaque(false);
        checksPanel.add(documentsVerifiedCheck);
        checksPanel.add(eligibilityVerifiedCheck);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        reviewCard.add(checksPanel, gbc);

        // Committee Remarks Input (Used for Missing Document / Clarification / Notes)
        JLabel remarksLabel = new JLabel("Committee Remarks:");
        remarksLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        reviewCard.add(remarksLabel, gbc);

        remarksArea = new JTextArea(2, 36);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        reviewCard.add(remarksScroll, gbc);

        // Verification Decision Action Buttons (UC-8 Step 5 & Flows A/B)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        buttonPanel.setOpaque(false);

        verifyButton = new JButton("Verified");
        verifyButton.setFont(BUTTON_FONT);
        verifyButton.setBackground(NAVY);
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setOpaque(true);
        verifyButton.setFocusPainted(false);
        verifyButton.setPreferredSize(new Dimension(115, 34));

        missingDocButton = new JButton("Request Missing Document");
        missingDocButton.setFont(BUTTON_FONT);
        missingDocButton.setPreferredSize(new Dimension(205, 34));

        clarifyButton = new JButton("Request Clarification");
        clarifyButton.setFont(BUTTON_FONT);
        clarifyButton.setPreferredSize(new Dimension(175, 34));

        buttonPanel.add(clarifyButton);
        buttonPanel.add(missingDocButton);
        buttonPanel.add(verifyButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 4;
        reviewCard.add(buttonPanel, gbc);

        JScrollPane reviewScroll = new JScrollPane(reviewCard);
        reviewScroll.setBorder(null);
        bodyPanel.add(reviewScroll, BorderLayout.CENTER);

        contentContainer.add(bodyPanel, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);

        // -------------------- EVENT HANDLERS --------------------
        openAppsButton.addActionListener(e -> loadApplicationsTable());

        applicationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedApplication();
            }
        });

        verifyButton.addActionListener(e -> verifySelectedApplication());
        missingDocButton.addActionListener(e -> requestMissingDocument());
        clarifyButton.addActionListener(e -> requestClarification());

        setVisible(true);
    }

    // Allows running VerifyApplicationFrame.java directly in VS Code
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerifyApplicationFrame());
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

    private void addTwoColumnRow(
        JPanel panel, GridBagConstraints gbc, int row,
        String label1, JComponent field1,
        String label2, JComponent field2
    ) {
        gbc.gridwidth = 1;
        gbc.gridy = row;

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

    // UC-8 Step 1 & 2: Load list of submitted student applications
    private void loadApplicationsTable() {
        tableModel.setRowCount(0);
        for (ApplicationRecord app : applications) {
            tableModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudentName(),
                app.getStudentId(),
                app.getProgram(),
                app.getCategory(),
                app.getStatus()
            });
        }
        selectedIndex = -1;
    }

    // UC-8 Step 3 & 4: Display selected student's read-only application details & documents
    private void displaySelectedApplication() {
        int row = applicationTable.getSelectedRow();
        if (row == -1) return;

        // Exception Flow: System cannot load documents
        if (simulateErrorCheck.isSelected()) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load documents.",
                "Document Load Error",
                JOptionPane.ERROR_MESSAGE
            );
            documentsArea.setText("Error: Unable to load documents.");
            return;
        }

        selectedIndex = row;
        ApplicationRecord app = applications.get(row);

        appIdField.setText(String.valueOf(app.getApplicationId()));
        regNoField.setText(app.getStudentId());
        studentNameField.setText(app.getStudentName());
        emailField.setText(app.getEmail());
        departmentField.setText(app.getDepartment());
        programField.setText(app.getProgram());
        scholarshipField.setText(app.getScholarshipName());
        categoryField.setText(app.getCategory());
        submissionDateField.setText(app.getSubmissionDate());
        cgpaField.setText(app.getCgpa());
        statusField.setText(app.getStatus());

        documentsArea.setText(app.getDocumentsSummary());
        documentsVerifiedCheck.setSelected(false);
        eligibilityVerifiedCheck.setSelected(app.isEligibilityVerified());
        remarksArea.setText(app.getCommitteeNotes());
    }

    // UC-8 Step 5 & 6 (Main Success Scenario)
    private void verifySelectedApplication() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please click 'Open Applications' and select a student application first.");
            return;
        }

        if (simulateErrorCheck.isSelected()) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load documents.",
                "Verification Stopped",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ApplicationRecord app = applications.get(selectedIndex);

        if (app.hasMissingDocuments()) {
            JOptionPane.showMessageDialog(
                this,
                "Required documents are missing. Please click 'Request Missing Document' instead.",
                "Missing Document Detected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!documentsVerifiedCheck.isSelected() || !eligibilityVerifiedCheck.isSelected()) {
            JOptionPane.showMessageDialog(
                this,
                "Please check both verification checkboxes before marking Verified.",
                "Verification Check Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        app.setStatus("Verified");
        app.setCommitteeNotes(remarksArea.getText().trim());
        tableModel.setValueAt("Verified", selectedIndex, 5);
        statusField.setText("Verified");

        JOptionPane.showMessageDialog(this, "The student verified successfully!");
    }

    // UC-8 Alternative Flow A: Missing Document
    private void requestMissingDocument() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please click 'Open Applications' and select a student application first.");
            return;
        }

        ApplicationRecord app = applications.get(selectedIndex);
        String note = remarksArea.getText().trim().isEmpty()
            ? "Required document is missing. Please upload the missing document."
            : remarksArea.getText().trim();

        app.setStatus("Incomplete");
        app.setCommitteeNotes(note);
        tableModel.setValueAt("Incomplete", selectedIndex, 5);
        statusField.setText("Incomplete");
        remarksArea.setText(note);

        JOptionPane.showMessageDialog(
            this,
            "Application marked as Incomplete.\nNotification sent to " + app.getStudentName()
            + " to upload missing documents."
        );
    }

    // UC-8 Alternative Flow B: Request Clarification
    private void requestClarification() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please click 'Open Applications' and select a student application first.");
            return;
        }

        if (remarksArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter what needs clarification in the Committee Remarks box."
            );
            return;
        }

        ApplicationRecord app = applications.get(selectedIndex);
        app.setStatus("Needs Clarification");
        app.setCommitteeNotes(remarksArea.getText().trim());
        tableModel.setValueAt("Needs Clarification", selectedIndex, 5);
        statusField.setText("Needs Clarification");

        JOptionPane.showMessageDialog(
            this,
            "Application marked as Needs Clarification.\nNotification sent to " + app.getStudentName()
            + " requesting clarification."
        );
    }

    private List<ApplicationRecord> createSampleApplications() {
        List<ApplicationRecord> list = new ArrayList<>();
        list.add(new ApplicationRecord(
            5001, "S-2026-014", "Ali Khan", "ali.khan@qau.edu.pk",
            "Computer Science", "BS", "Need Based Scholarship BS", "Need based",
            "15/10/2026", "3.65", "Pending",
            "Marksheets/Degree: ali_khan_marksheet.pdf (PDF)\n"
          + "Bills: utility_bills_oct.pdf (PDF)\n"
          + "ID Card: student_id_5001.pdf (PDF)\n"
          + "Income Slip: father_income_slip.docx (DOCX)",
            false
        ));
        list.add(new ApplicationRecord(
            5002, "S-2026-015", "Sara Ahmed", "sara.ahmed@qau.edu.pk",
            "Computer Science", "BS", "Merit Based Scholarship BS", "Merit based",
            "16/10/2026", "3.92", "Pending",
            "Marksheets/Degree: sara_transcript_sem4.pdf (PDF)\n"
          + "ID Card: qau_id_5002.pdf (PDF)\n"
          + "Bills: Not required (Merit Based)\n"
          + "Income Slip: Not required (Merit Based)",
            false
        ));
        list.add(new ApplicationRecord(
            5003, "S-2026-016", "Bilal Tariq", "bilal.tariq@qau.edu.pk",
            "Computer Science", "BS", "Need Based Scholarship BS", "Need based",
            "17/10/2026", "3.78", "Pending",
            "Marksheets/Degree: bilal_marksheet.pdf (PDF)\n"
          + "Bills: electricity_gas_bills.pdf (PDF)\n"
          + "ID Card: bilal_id_card.pdf (PDF)\n"
          + "Income Slip: MISSING",
            true
        ));
        list.add(new ApplicationRecord(
            5004, "S-2026-017", "Ayesha Siddiqui", "ayesha.siddiqui@qau.edu.pk",
            "Computer Science", "BS", "Merit Based Scholarship BS", "Merit based",
            "18/10/2026", "3.84", "Pending",
            "Marksheets/Degree: ayesha_semester_result.docx (DOCX)\n"
          + "Bills: home_utility_bill.pdf (PDF)\n"
          + "ID Card: ayesha_qau_card.pdf (PDF)\n"
          + "Income Slip: income_statement.pdf (PDF)",
            false
        ));
        return list;
    }

    // Embedded model class so VerifyApplicationFrame.java compiles with zero external files
    private static class ApplicationRecord {
        private int applicationId;
        private String studentId;
        private String studentName;
        private String email;
        private String department;
        private String program;
        private String scholarshipName;
        private String category;
        private String submissionDate;
        private String cgpa;
        private String status;
        private String documentsSummary;
        private boolean missingDocuments;
        private boolean eligibilityVerified;
        private String committeeNotes;

        public ApplicationRecord(
            int applicationId, String studentId, String studentName, String email,
            String department, String program, String scholarshipName, String category,
            String submissionDate, String cgpa, String status,
            String documentsSummary, boolean missingDocuments
        ) {
            this.applicationId = applicationId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.email = email;
            this.department = department;
            this.program = program;
            this.scholarshipName = scholarshipName;
            this.category = category;
            this.submissionDate = submissionDate;
            this.cgpa = cgpa;
            this.status = status;
            this.documentsSummary = documentsSummary;
            this.missingDocuments = missingDocuments;
            this.eligibilityVerified = false;
            this.committeeNotes = "";
        }

        public int getApplicationId() { return applicationId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getProgram() { return program; }
        public String getScholarshipName() { return scholarshipName; }
        public String getCategory() { return category; }
        public String getSubmissionDate() { return submissionDate; }
        public String getCgpa() { return cgpa; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDocumentsSummary() { return documentsSummary; }
        public boolean hasMissingDocuments() { return missingDocuments; }
        public boolean isEligibilityVerified() { return eligibilityVerified; }
        public String getCommitteeNotes() { return committeeNotes; }
        public void setCommitteeNotes(String committeeNotes) { this.committeeNotes = committeeNotes; }
    }
}