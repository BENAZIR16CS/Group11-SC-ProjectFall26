import javax.swing.*;                       // Provides GUI components like JFrame, JTable, JLabel, JButton
import javax.swing.table.DefaultTableModel; // Manages rows inside the student's tracked applications table
import java.awt.*;                          // Provides layouts, fonts, colors, and spacing
import java.awt.event.ActionListener;       // Handles button click events
import java.util.ArrayList;                 // Stores submitted student applications
import java.util.List;

// TrackApplicationFrame implements Use Case 6 / UC-7: "Track Application Status"
// Self-contained single file: compiles and runs directly in VS Code with no conflicts
// with other files in your project (such as Theme.java).
public class TrackApplicationFrame extends JFrame {

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
    private static final Color STEP_DONE_BG = new Color(236, 253, 245);
    private static final Color STEP_DONE_BORDER = new Color(16, 185, 129);
    private static final Color STEP_ACTIVE_BG = new Color(230, 240, 250);
    private static final Color STEP_WARN_BG = new Color(255, 251, 235);
    private static final Color STEP_REJECT_BG = new Color(255, 241, 242);

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

    // Sample application repository and filtered list for the active student (NFR-04)
    private List<TrackedApplication> allApplications;
    private List<TrackedApplication> myApplications;

    // Student Lookup Inputs (UC-6 Input: Student ID & optional Application No)
    private JTextField studentIdInputField;
    private JTextField appIdInputField;
    private JButton trackButton;
    private JButton clearLookupButton;
    private JButton refreshButton;

    // Applications Status Table (FR-26: Application ID, Scholarship Name, Status & Progression)
    private JTable statusTable;
    private DefaultTableModel tableModel;

    // Progression Stepper Labels (4 stages)
    private JLabel[] stepLabels;

    // Read-Only Details Card
    private JTextField appIdField;
    private JTextField statusField;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField scholarshipField;
    private JTextField categoryField;
    private JTextField programField;
    private JTextField submissionDateField;
    private JTextField stageField;
    private JTextField lastUpdatedField;
    private JTextArea remarksArea;
    private JButton okButton;

    // Constructor: builds the 1000x700 SMS Track Application screen
    public TrackApplicationFrame() {

        setTitle("SMS - Track Application");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        allApplications = createSampleApplications();
        myApplications = new ArrayList<>();

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
        sidebarTop.add(createSidebarButton("Submit Application", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-5: Submit Application screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("Track Application", true, e -> trackApplications()));
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

        JLabel pageTitle = new JLabel("Track Application Status");
        pageTitle.setFont(HEADER_FONT);
        pageTitle.setForeground(TEXT_DARK);
        titleGroup.add(pageTitle);

        JLabel pageSubtitle = new JLabel(
            "Enter your Student ID (and optional Application No) to track your applications and progression status."
        );
        pageSubtitle.setFont(SUBTITLE_FONT);
        pageSubtitle.setForeground(TEXT_MUTED);
        titleGroup.add(pageSubtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(BUTTON_FONT);
        refreshButton.setPreferredSize(new Dimension(110, 32));
        headerPanel.add(refreshButton, BorderLayout.EAST);

        contentContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Body: Lookup + Applications Table on top, Progression + Details Card below
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 12));
        bodyPanel.setOpaque(false);

        // 1) Lookup Bar + My Applications Table Card
        JPanel listCard = new JPanel(new BorderLayout(0, 8));
        listCard.setBackground(CARD_BG);
        listCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel lookupBar = new JPanel(new BorderLayout(8, 6));
        lookupBar.setOpaque(false);

        JPanel lookupInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        lookupInputs.setOpaque(false);

        JLabel sidLabel = new JLabel("Student ID *:");
        sidLabel.setFont(LABEL_FONT);
        studentIdInputField = new JTextField("S-2026-014", 12);
        studentIdInputField.setFont(SMALL_FONT);

        JLabel aidLabel = new JLabel("Application No (Optional):");
        aidLabel.setFont(LABEL_FONT);
        appIdInputField = new JTextField(9);
        appIdInputField.setFont(SMALL_FONT);

        trackButton = new JButton("Track Application");
        trackButton.setFont(BUTTON_FONT);
        trackButton.setBackground(NAVY);
        trackButton.setForeground(Color.WHITE);
        trackButton.setOpaque(true);
        trackButton.setFocusPainted(false);

        clearLookupButton = new JButton("Clear");
        clearLookupButton.setFont(SMALL_FONT);

        lookupInputs.add(sidLabel);
        lookupInputs.add(studentIdInputField);
        lookupInputs.add(aidLabel);
        lookupInputs.add(appIdInputField);
        lookupInputs.add(trackButton);
        lookupInputs.add(clearLookupButton);
        lookupBar.add(lookupInputs, BorderLayout.CENTER);

        listCard.add(lookupBar, BorderLayout.NORTH);

        String[] columns = {"App ID", "Scholarship Name", "Category", "Program", "Submitted Date", "Status", "Progression"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statusTable = new JTable(tableModel);
        statusTable.setRowHeight(24);
        statusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(statusTable);
        tableScroll.setPreferredSize(new Dimension(0, 125));
        listCard.add(tableScroll, BorderLayout.CENTER);

        bodyPanel.add(listCard, BorderLayout.NORTH);

        // 2) Selected Application Progression + Details Card
        JPanel detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBackground(CARD_BG);
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Progression Stepper Panel (4 steps)
        JPanel stepperPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        stepperPanel.setOpaque(false);
        String[] stepTitles = {
            "1. Submitted",
            "2. Under Review",
            "3. Verified / Interview",
            "4. Final Decision"
        };
        stepLabels = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            JLabel step = new JLabel(stepTitles[i], SwingConstants.CENTER);
            step.setFont(LABEL_FONT);
            step.setOpaque(true);
            step.setBackground(READONLY_BG);
            step.setForeground(TEXT_MUTED);
            step.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ));
            stepLabels[i] = step;
            stepperPanel.add(step);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        detailsCard.add(stepperPanel, gbc);

        appIdField = createReadOnlyField();
        statusField = createReadOnlyField();
        studentIdField = createReadOnlyField();
        studentNameField = createReadOnlyField();
        scholarshipField = createReadOnlyField();
        categoryField = createReadOnlyField();
        programField = createReadOnlyField();
        submissionDateField = createReadOnlyField();
        stageField = createReadOnlyField();
        lastUpdatedField = createReadOnlyField();

        addTwoColumnRow(detailsCard, gbc, 1, "Application ID:", appIdField, "Application Status:", statusField);
        addTwoColumnRow(detailsCard, gbc, 2, "Student ID:", studentIdField, "Student Name:", studentNameField);
        addTwoColumnRow(detailsCard, gbc, 3, "Scholarship:", scholarshipField, "Category (BR-01):", categoryField);
        addTwoColumnRow(detailsCard, gbc, 4, "Program:", programField, "Submission Date:", submissionDateField);
        addTwoColumnRow(detailsCard, gbc, 5, "Progression Stage:", stageField, "Last Updated:", lastUpdatedField);

        JLabel remarksLabel = new JLabel("Status Remarks:");
        remarksLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        detailsCard.add(remarksLabel, gbc);

        remarksArea = new JTextArea(3, 36);
        remarksArea.setEditable(false);
        remarksArea.setBackground(READONLY_BG);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        detailsCard.add(remarksScroll, gbc);

        // UC-6 Step 3 & 4: Student clicks OK to acknowledge / close detail selection
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setOpaque(false);

        okButton = new JButton("OK");
        okButton.setFont(BUTTON_FONT);
        okButton.setBackground(NAVY);
        okButton.setForeground(Color.WHITE);
        okButton.setOpaque(true);
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(110, 32));
        actionPanel.add(okButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        detailsCard.add(actionPanel, gbc);

        JScrollPane detailsScroll = new JScrollPane(detailsCard);
        detailsScroll.setBorder(null);
        bodyPanel.add(detailsScroll, BorderLayout.CENTER);

        contentContainer.add(bodyPanel, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);

        // -------------------- EVENT HANDLERS --------------------
        trackButton.addActionListener(e -> trackApplications());
        refreshButton.addActionListener(e -> trackApplications());
        clearLookupButton.addActionListener(e -> {
            studentIdInputField.setText("");
            appIdInputField.setText("");
            tableModel.setRowCount(0);
            myApplications.clear();
            clearSelectedDetails();
        });

        statusTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedApplication();
            }
        });

        okButton.addActionListener(e -> handleOkAction());

        setVisible(true);
    }

    // Allows running TrackApplicationFrame.java directly in VS Code
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrackApplicationFrame());
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

    // UC-6 Steps 1-2, FR-26, NFR-04, Alternate 7.1, and Flow A exception handling
    private void trackApplications() {
        String enteredStudentId = studentIdInputField.getText().trim();
        String enteredAppNo = appIdInputField.getText().trim();

        if (enteredStudentId.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter your Student ID to track your applications (NFR-04).",
                "Student ID Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Integer filterAppId = null;
        if (!enteredAppNo.isEmpty()) {
            try {
                filterAppId = Integer.parseInt(enteredAppNo);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid Application No. Please enter a numeric Application ID (for example: 5001).",
                    "Invalid Application No",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        tableModel.setRowCount(0);
        myApplications.clear();
        clearSelectedDetails();

        try {
            if (allApplications == null) {
                throw new IllegalStateException("Application tracking service unavailable");
            }

            // NFR-04: Filter strictly to the authenticated/entered student's own applications
            for (TrackedApplication app : allApplications) {
                if (!app.getStudentId().equalsIgnoreCase(enteredStudentId)) {
                    continue;
                }
                if (filterAppId != null && app.getApplicationId() != filterAppId) {
                    continue;
                }
                myApplications.add(app);
                tableModel.addRow(new Object[]{
                    app.getApplicationId(),
                    app.getScholarshipName(),
                    app.getCategory(),
                    app.getProgram(),
                    app.getSubmissionDate(),
                    app.getStatus(),
                    app.getStageLabel()
                });
            }

            // Alternate 7.1: Inform student when no applications exist
            if (myApplications.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No submitted applications found for Student ID: " + enteredStudentId
                    + (filterAppId != null ? " (App ID: " + filterAppId + ")" : "") + ".",
                    "No Applications Found",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            statusTable.setRowSelectionInterval(0, 0);
            displaySelectedApplication();
        } catch (Exception ex) {
            // UC-6 Alternative Flow A: High load / tracking failure
            JOptionPane.showMessageDialog(
                this,
                "Unable to Track, Try Again",
                "Tracking Error (Flow A)",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displaySelectedApplication() {
        int row = statusTable.getSelectedRow();
        if (row < 0 || row >= myApplications.size()) {
            return;
        }

        TrackedApplication app = myApplications.get(row);
        appIdField.setText(String.valueOf(app.getApplicationId()));
        statusField.setText(app.getStatus());
        studentIdField.setText(app.getStudentId());
        studentNameField.setText(app.getStudentName());
        scholarshipField.setText(app.getScholarshipName());
        categoryField.setText(app.getCategory());
        programField.setText(app.getProgram());
        submissionDateField.setText(app.getSubmissionDate());
        stageField.setText(app.getStageLabel());
        lastUpdatedField.setText(app.getLastUpdated());
        remarksArea.setText(app.getRemarks());

        updateStepperVisual(app.getStageIndex(), app.getStatus());
    }

    private void updateStepperVisual(int activeStage, String status) {
        boolean isWarning = "Incomplete".equalsIgnoreCase(status)
            || "Needs Clarification".equalsIgnoreCase(status);
        boolean isRejected = "Rejected".equalsIgnoreCase(status);

        for (int i = 0; i < stepLabels.length; i++) {
            int stepNumber = i + 1;
            JLabel lbl = stepLabels[i];
            if (stepNumber < activeStage) {
                lbl.setBackground(STEP_DONE_BG);
                lbl.setForeground(new Color(4, 120, 87));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(STEP_DONE_BORDER),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
            } else if (stepNumber == activeStage) {
                Color bg = isRejected ? STEP_REJECT_BG : (isWarning ? STEP_WARN_BG : STEP_ACTIVE_BG);
                Color fg = isRejected ? new Color(190, 18, 60) : (isWarning ? new Color(180, 83, 9) : NAVY);
                lbl.setBackground(bg);
                lbl.setForeground(fg);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fg),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
            } else {
                lbl.setBackground(READONLY_BG);
                lbl.setForeground(TEXT_MUTED);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_GRAY),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
            }
        }
    }

    private void clearSelectedDetails() {
        appIdField.setText("");
        statusField.setText("");
        studentIdField.setText("");
        studentNameField.setText("");
        scholarshipField.setText("");
        categoryField.setText("");
        programField.setText("");
        submissionDateField.setText("");
        stageField.setText("");
        lastUpdatedField.setText("");
        remarksArea.setText("");
        updateStepperVisual(0, "");
    }

    // UC-6 Step 3 & 4: Student selects OK to finish viewing status and return
    private void handleOkAction() {
        if (statusTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Click 'Track Application' and select an application row first."
            );
            return;
        }
        statusTable.clearSelection();
        clearSelectedDetails();
        JOptionPane.showMessageDialog(
            this,
            "Status check acknowledged. Returning to Track Application overview."
        );
    }

    private List<TrackedApplication> createSampleApplications() {
        List<TrackedApplication> list = new ArrayList<>();
        list.add(new TrackedApplication(
            5001, "S-2026-014", "Ali Khan", "Need Based Scholarship BS", "Need based", "BS",
            "15/10/2026", "Pending", 1, "1. Submitted", "15/10/2026",
            "Your application has been submitted and is waiting for Scholarship Committee verification."
        ));
        list.add(new TrackedApplication(
            5002, "S-2026-015", "Sara Ahmed", "Merit Based Scholarship BS", "Merit based", "BS",
            "16/10/2026", "Approved", 4, "4. Selected / Approved", "24/10/2026",
            "Congratulations! Your application has been Approved. Per BR-04, no additional scholarship applications are allowed."
        ));
        list.add(new TrackedApplication(
            5003, "S-2026-016", "Bilal Tariq", "Need Based Scholarship BS", "Need based", "BS",
            "17/10/2026", "Incomplete", 2, "2. Action Required (Incomplete)", "19/10/2026",
            "Committee Remark: Income Slip is missing. Please upload your Father/Guardian Income Slip in PDF or DOCX format."
        ));
        list.add(new TrackedApplication(
            5004, "S-2026-017", "Ayesha Siddiqui", "Merit Based Scholarship BS", "Merit based", "BS",
            "18/10/2026", "Verified", 3, "3. Under Review / Verified", "21/10/2026",
            "Documents and eligibility verified by the Scholarship Committee. Awaiting online interview schedule."
        ));
        list.add(new TrackedApplication(
            5006, "S-2026-014", "Ali Khan", "Merit Based Scholarship BS", "Merit based", "BS",
            "12/10/2026", "Rejected", 4, "4. Decision Finalized (Rejected)", "22/10/2026",
            "Your application was not selected for Merit Based Scholarship BS. Open View Feedback for detailed remarks."
        ));
        return list;
    }

    // Embedded model class so TrackApplicationFrame.java compiles with zero external files
    private static class TrackedApplication {
        private int applicationId;
        private String studentId;
        private String studentName;
        private String scholarshipName;
        private String category;
        private String program;
        private String submissionDate;
        private String status;
        private int stageIndex;
        private String stageLabel;
        private String lastUpdated;
        private String remarks;

        public TrackedApplication(
            int applicationId, String studentId, String studentName,
            String scholarshipName, String category, String program,
            String submissionDate, String status, int stageIndex,
            String stageLabel, String lastUpdated, String remarks
        ) {
            this.applicationId = applicationId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.scholarshipName = scholarshipName;
            this.category = category;
            this.program = program;
            this.submissionDate = submissionDate;
            this.status = status;
            this.stageIndex = stageIndex;
            this.stageLabel = stageLabel;
            this.lastUpdated = lastUpdated;
            this.remarks = remarks;
        }

        public int getApplicationId() { return applicationId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getScholarshipName() { return scholarshipName; }
        public String getCategory() { return category; }
        public String getProgram() { return program; }
        public String getSubmissionDate() { return submissionDate; }
        public String getStatus() { return status; }
        public int getStageIndex() { return stageIndex; }
        public String getStageLabel() { return stageLabel; }
        public String getLastUpdated() { return lastUpdated; }
        public String getRemarks() { return remarks; }
    }
}