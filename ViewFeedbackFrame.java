import javax.swing.*;                       // Provides GUI components like JFrame, JTable, JLabel, JButton
import javax.swing.table.DefaultTableModel; // Manages rows inside the logged-in student's feedback table
import java.awt.*;                          // Provides layouts, fonts, colors, and spacing
import java.awt.event.ActionListener;       // Handles button click events
import java.util.ArrayList;                 // Stores application feedback records
import java.util.List;

// ViewFeedbackFrame implements Use Case 12: "View Feedback"
// Shows feedback strictly for the currently logged-in student only (NFR-04).
// Self-contained single file: compiles and runs directly in VS Code with no conflicts
// with other files in your project (such as Theme.java).
public class ViewFeedbackFrame extends JFrame {

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

    // Logged-in student's ID (passed from Login screen; defaults to S-2026-015 for standalone run)
    private final String loggedInStudentId;

    // Feedback repository and filtered list containing ONLY the logged-in student's applications
    private List<FeedbackRecord> allRecords;
    private List<FeedbackRecord> myRecords;

    // Header & My Applications Table (UC-12 Input: click View Feedback & select own application)
    private JButton viewFeedbackButton;
    private JLabel sessionBadgeLabel;
    private JTable applicationTable;
    private DefaultTableModel tableModel;

    // Read-Only Feedback Details Fields (UC-12 Output & Domain Model Feedback entity)
    private JTextField appIdField;
    private JTextField decisionField;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField scholarshipField;
    private JTextField categoryField;
    private JTextField interviewScoreField;
    private JTextField feedbackDateField;
    private JTextField writerField;
    private JTextField statusField;
    private JTextArea goodPointsArea;
    private JTextArea remarksArea;
    private JButton okButton;

    // Default constructor for standalone run in VS Code
    public ViewFeedbackFrame() {
        this("S-2026-015");
    }

    // Overloaded constructor called after Student Login so only that student's feedback is shown
    public ViewFeedbackFrame(String loggedInStudentId) {
        this.loggedInStudentId = (loggedInStudentId == null || loggedInStudentId.trim().isEmpty())
            ? "S-2026-015"
            : loggedInStudentId.trim();

        setTitle("SMS - View Feedback");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        allRecords = createSampleFeedbackRecords();
        myRecords = new ArrayList<>();

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

        JLabel subRoleLabel = new JLabel("Student ID: " + this.loggedInStudentId);
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
        sidebarTop.add(createSidebarButton("Track Application", false, e ->
            JOptionPane.showMessageDialog(this, "Opens UC-6: Track Application Status screen.")
        ));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarButton("View Feedback", true, e -> loadMyFeedback()));

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

        JLabel pageTitle = new JLabel("View My Application Feedback");
        pageTitle.setFont(HEADER_FONT);
        pageTitle.setForeground(TEXT_DARK);
        titleGroup.add(pageTitle);

        JLabel pageSubtitle = new JLabel(
            "Click View Feedback to review decision, interview score, and committee remarks for your application."
        );
        pageSubtitle.setFont(SUBTITLE_FONT);
        pageSubtitle.setForeground(TEXT_MUTED);
        titleGroup.add(pageSubtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        viewFeedbackButton = new JButton("View Feedback");
        viewFeedbackButton.setFont(BUTTON_FONT);
        viewFeedbackButton.setBackground(NAVY);
        viewFeedbackButton.setForeground(Color.WHITE);
        viewFeedbackButton.setOpaque(true);
        viewFeedbackButton.setFocusPainted(false);
        viewFeedbackButton.setPreferredSize(new Dimension(145, 32));
        headerPanel.add(viewFeedbackButton, BorderLayout.EAST);

        contentContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Body: Logged-in Student Applications Table on top, Feedback Details Card below
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 12));
        bodyPanel.setOpaque(false);

        // 1) My Submitted Applications Table Card (strictly scoped to loggedInStudentId)
        JPanel listCard = new JPanel(new BorderLayout(0, 8));
        listCard.setBackground(CARD_BG);
        listCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel listHeaderBar = new JPanel(new BorderLayout());
        listHeaderBar.setOpaque(false);

        JLabel tableTitle = new JLabel("My Submitted Applications");
        tableTitle.setFont(SECTION_FONT);
        tableTitle.setForeground(TEXT_DARK);
        listHeaderBar.add(tableTitle, BorderLayout.WEST);

        sessionBadgeLabel = new JLabel("Logged-in Student ID: " + this.loggedInStudentId);
        sessionBadgeLabel.setFont(LABEL_FONT);
        sessionBadgeLabel.setForeground(TEXT_MUTED);
        listHeaderBar.add(sessionBadgeLabel, BorderLayout.EAST);

        listCard.add(listHeaderBar, BorderLayout.NORTH);

        String[] columns = {"App ID", "Scholarship Name", "Category", "Decision", "Interview Score", "Feedback Status"};
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
        tableScroll.setPreferredSize(new Dimension(0, 110));
        listCard.add(tableScroll, BorderLayout.CENTER);

        bodyPanel.add(listCard, BorderLayout.NORTH);

        // 2) Committee Feedback Details Card (Read-Only)
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

        appIdField = createReadOnlyField();
        decisionField = createReadOnlyField();
        studentIdField = createReadOnlyField();
        studentNameField = createReadOnlyField();
        scholarshipField = createReadOnlyField();
        categoryField = createReadOnlyField();
        interviewScoreField = createReadOnlyField();
        feedbackDateField = createReadOnlyField();
        writerField = createReadOnlyField();
        statusField = createReadOnlyField();

        addTwoColumnRow(detailsCard, gbc, 0, "Application ID:", appIdField, "Decision:", decisionField);
        addTwoColumnRow(detailsCard, gbc, 1, "Student ID:", studentIdField, "Student Name:", studentNameField);
        addTwoColumnRow(detailsCard, gbc, 2, "Scholarship:", scholarshipField, "Category (BR-01):", categoryField);
        addTwoColumnRow(detailsCard, gbc, 3, "Interview Score:", interviewScoreField, "Feedback Date:", feedbackDateField);
        addTwoColumnRow(detailsCard, gbc, 4, "Feedback Writer:", writerField, "Feedback Status:", statusField);

        JLabel goodLabel = new JLabel("Strengths / Good Points:");
        goodLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        detailsCard.add(goodLabel, gbc);

        goodPointsArea = new JTextArea(2, 36);
        goodPointsArea.setEditable(false);
        goodPointsArea.setBackground(READONLY_BG);
        goodPointsArea.setLineWrap(true);
        goodPointsArea.setWrapStyleWord(true);
        JScrollPane goodScroll = new JScrollPane(goodPointsArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        detailsCard.add(goodScroll, gbc);

        JLabel remarksLabel = new JLabel("Remarks / To Improve:");
        remarksLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        detailsCard.add(remarksLabel, gbc);

        remarksArea = new JTextArea(2, 36);
        remarksArea.setEditable(false);
        remarksArea.setBackground(READONLY_BG);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        detailsCard.add(remarksScroll, gbc);

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
        viewFeedbackButton.addActionListener(e -> loadMyFeedback());

        applicationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedFeedback();
            }
        });

        okButton.addActionListener(e -> handleOkAction());

        setVisible(true);
    }

    // Allows running ViewFeedbackFrame.java directly in VS Code
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewFeedbackFrame("S-2026-015"));
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

    // UC-12 Step 1 & Exception Flow: Load ONLY the logged-in student's applications & feedback
    private void loadMyFeedback() {
        tableModel.setRowCount(0);
        myRecords.clear();
        clearFeedbackDetails();

        try {
            if (allRecords == null) {
                throw new IllegalStateException("Feedback repository unavailable");
            }

            // Strictly filter by the logged-in student's ID (NFR-04)
            for (FeedbackRecord rec : allRecords) {
                if (rec.getStudentId().equalsIgnoreCase(loggedInStudentId)) {
                    myRecords.add(rec);
                    tableModel.addRow(new Object[]{
                        rec.getApplicationId(),
                        rec.getScholarshipName(),
                        rec.getCategory(),
                        rec.getDecision(),
                        rec.getInterviewScore(),
                        rec.hasFeedback() ? "Available" : "Pending Feedback"
                    });
                }
            }

            if (myRecords.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No feedback provided yet!",
                    "No Feedback Found",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // Automatically select the logged-in student's application and show feedback (or Alternative Flow)
            applicationTable.setRowSelectionInterval(0, 0);
        } catch (Exception ex) {
            // UC-12 Exception Flow: System cannot load feedback
            JOptionPane.showMessageDialog(
                this,
                "Unable to load feedback. Please try again later",
                "Feedback Load Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // UC-12 Step 2 & Alternative Flow: Display feedback or inform student when none exists yet
    private void displaySelectedFeedback() {
        int row = applicationTable.getSelectedRow();
        if (row < 0 || row >= myRecords.size()) {
            return;
        }

        FeedbackRecord rec = myRecords.get(row);
        if (!rec.hasFeedback()) {
            clearFeedbackDetails();
            JOptionPane.showMessageDialog(
                this,
                "No feedback provided yet!",
                "Feedback Not Available",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        appIdField.setText(String.valueOf(rec.getApplicationId()));
        decisionField.setText(rec.getDecision());
        studentIdField.setText(rec.getStudentId());
        studentNameField.setText(rec.getStudentName());
        scholarshipField.setText(rec.getScholarshipName());
        categoryField.setText(rec.getCategory());
        interviewScoreField.setText(rec.getInterviewScore());
        feedbackDateField.setText(rec.getFeedbackDate());
        writerField.setText(rec.getWriter());
        statusField.setText("Feedback Generated");
        goodPointsArea.setText(rec.getGoodPoints());
        remarksArea.setText(rec.getRemarks());
    }

    private void clearFeedbackDetails() {
        appIdField.setText("");
        decisionField.setText("");
        studentIdField.setText("");
        studentNameField.setText("");
        scholarshipField.setText("");
        categoryField.setText("");
        interviewScoreField.setText("");
        feedbackDateField.setText("");
        writerField.setText("");
        statusField.setText("");
        goodPointsArea.setText("");
        remarksArea.setText("");
    }

    private void handleOkAction() {
        if (applicationTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please click 'View Feedback' first."
            );
            return;
        }
        applicationTable.clearSelection();
        clearFeedbackDetails();
        JOptionPane.showMessageDialog(
            this,
            "Feedback review completed."
        );
    }

    private List<FeedbackRecord> createSampleFeedbackRecords() {
        List<FeedbackRecord> list = new ArrayList<>();
        list.add(new FeedbackRecord(
            5001, "S-2026-014", "Ali Khan", "Need Based Scholarship BS", "Need based",
            "Pending Result", "Pending Interview", "", "", false, "", ""
        ));
        list.add(new FeedbackRecord(
            5002, "S-2026-015", "Sara Ahmed", "Merit Based Scholarship BS", "Merit based",
            "Approved", "91 / 100", "24/10/2026", "Scholarship Committee", true,
            "Strong academic standing (CGPA 3.92) with complete transcript and clear interview responses.",
            "Selected for Merit Based Scholarship BS. Maintain >= 3.50 CGPA each semester."
        ));
        list.add(new FeedbackRecord(
            5003, "S-2026-016", "Bilal Tariq", "Need Based Scholarship BS", "Need based",
            "Incomplete", "Not Conducted", "19/10/2026", "Scholarship Committee", true,
            "Good CGPA (3.78) and valid electricity/gas utility bills submitted on time.",
            "Upload missing Father/Guardian Income Slip in PDF or DOCX format to proceed to interview."
        ));
        list.add(new FeedbackRecord(
            5004, "S-2026-017", "Ayesha Siddiqui", "Merit Based Scholarship BS", "Merit based",
            "Not Approved", "76 / 100", "25/10/2026", "Scholarship Committee", true,
            "All academic transcripts and documents were properly organized and verified.",
            "Competition seats were filled by higher merit rank; strengthen project portfolio for next cycle."
        ));
        return list;
    }

    // Embedded model class so ViewFeedbackFrame.java compiles with zero external files
    private static class FeedbackRecord {
        private int applicationId;
        private String studentId;
        private String studentName;
        private String scholarshipName;
        private String category;
        private String decision;
        private String interviewScore;
        private String feedbackDate;
        private String writer;
        private boolean feedbackExists;
        private String goodPoints;
        private String remarks;

        public FeedbackRecord(
            int applicationId, String studentId, String studentName,
            String scholarshipName, String category, String decision,
            String interviewScore, String feedbackDate, String writer,
            boolean feedbackExists, String goodPoints, String remarks
        ) {
            this.applicationId = applicationId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.scholarshipName = scholarshipName;
            this.category = category;
            this.decision = decision;
            this.interviewScore = interviewScore;
            this.feedbackDate = feedbackDate;
            this.writer = writer;
            this.feedbackExists = feedbackExists;
            this.goodPoints = goodPoints;
            this.remarks = remarks;
        }

        public int getApplicationId() { return applicationId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getScholarshipName() { return scholarshipName; }
        public String getCategory() { return category; }
        public String getDecision() { return decision; }
        public String getInterviewScore() { return interviewScore; }
        public String getFeedbackDate() { return feedbackDate; }
        public String getWriter() { return writer; }
        public boolean hasFeedback() { return feedbackExists; }
        public String getGoodPoints() { return goodPoints; }
        public String getRemarks() { return remarks; }
    }
}