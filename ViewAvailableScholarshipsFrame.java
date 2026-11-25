import javax.swing.*;                       // Provides GUI components like JFrame, JTable, JLabel, JButton
import javax.swing.table.DefaultTableModel; // Manages rows inside the available scholarships table
import java.awt.*;                          // Provides layouts, fonts, colors, and spacing
import java.awt.event.ActionListener;       // Handles button and filter events
import java.util.ArrayList;                 // Stores scholarship records
import java.util.List;

// ViewAvailableScholarshipsFrame implements the Student Use Case: "View Available Scholarships"
// Self-contained single file: compiles and runs directly in VS Code with no conflicts
// with other files in your project (such as Theme.java).
public class ViewAvailableScholarshipsFrame extends JFrame {

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

    // Master list of scholarships and currently displayed filtered list
    private List<ScholarshipRecord> allScholarships;
    private List<ScholarshipRecord> displayedScholarships;
    private boolean scholarshipsLoaded = false;

    // Filter controls (Alternative Flow A: Category & Program filter)
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> programFilterCombo;

    // Available Scholarships Table (Step 3: displays name, category, program, deadline)
    private JTable scholarshipTable;
    private DefaultTableModel tableModel;

    // Read-only detail fields when student selects a scholarship (FR-15)
    private JTextField idField;
    private JTextField nameField;
    private JTextField categoryField;
    private JTextField programField;
    private JTextField startDateField;
    private JTextField deadlineField;
    private JTextField statusField;
    private JTextArea detailsArea;

    private JButton viewButton;
    private JButton resetFilterButton;
    private JButton checkEligibilityButton;
    private JButton applyButton;

    // Constructor: builds the 1000x700 SMS View Available Scholarships screen
    public ViewAvailableScholarshipsFrame() {

        setTitle("SMS - View Available Scholarships");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        allScholarships = createSampleScholarships();
        displayedScholarships = new ArrayList<>();

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

        // Student Sidebar Navigation Options (matching group's Student Dashboard)
        sidebarTop.add(createSidebarButton("View Available Scholarships", true, e -> loadAvailableScholarships()));
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

        JLabel pageTitle = new JLabel("View Available Scholarships");
        pageTitle.setFont(HEADER_FONT);
        pageTitle.setForeground(TEXT_DARK);
        titleGroup.add(pageTitle);

        JLabel pageSubtitle = new JLabel(
            "Browse advertised scholarships, filter by category or program, and select a row to view full details."
        );
        pageSubtitle.setFont(SUBTITLE_FONT);
        pageSubtitle.setForeground(TEXT_MUTED);
        titleGroup.add(pageSubtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topControls.setOpaque(false);

        viewButton = new JButton("View Scholarships");
        viewButton.setFont(BUTTON_FONT);
        viewButton.setBackground(NAVY);
        viewButton.setForeground(Color.WHITE);
        viewButton.setOpaque(true);
        viewButton.setFocusPainted(false);
        viewButton.setPreferredSize(new Dimension(155, 32));

        topControls.add(viewButton);
        headerPanel.add(topControls, BorderLayout.EAST);

        contentContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Body: Filter + Table Card on top, Selected Scholarship Details Card below
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 12));
        bodyPanel.setOpaque(false);

        // 1) Filter Bar + Advertised Scholarships Table Card
        JPanel listCard = new JPanel(new BorderLayout(0, 8));
        listCard.setBackground(CARD_BG);
        listCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);

        JLabel tableTitle = new JLabel("Advertised Scholarships (Visible to Students)");
        tableTitle.setFont(SECTION_FONT);
        tableTitle.setForeground(TEXT_DARK);
        filterBar.add(tableTitle, BorderLayout.WEST);

        JPanel filterInputs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterInputs.setOpaque(false);

        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(LABEL_FONT);
        categoryFilterCombo = new JComboBox<>(new String[]{"All Categories", "Need based", "Merit based"});
        categoryFilterCombo.setFont(SMALL_FONT);

        JLabel progLabel = new JLabel("Program:");
        progLabel.setFont(LABEL_FONT);
        programFilterCombo = new JComboBox<>(new String[]{"All Programs", "BS", "MS", "MPhil", "PhD"});
        programFilterCombo.setFont(SMALL_FONT);

        resetFilterButton = new JButton("Reset");
        resetFilterButton.setFont(SMALL_FONT);

        filterInputs.add(catLabel);
        filterInputs.add(categoryFilterCombo);
        filterInputs.add(progLabel);
        filterInputs.add(programFilterCombo);
        filterInputs.add(resetFilterButton);
        filterBar.add(filterInputs, BorderLayout.EAST);

        listCard.add(filterBar, BorderLayout.NORTH);

        String[] columns = {"ID", "Scholarship Name", "Category", "Program", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scholarshipTable = new JTable(tableModel);
        scholarshipTable.setRowHeight(24);
        scholarshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(scholarshipTable);
        tableScroll.setPreferredSize(new Dimension(0, 145));
        listCard.add(tableScroll, BorderLayout.CENTER);

        bodyPanel.add(listCard, BorderLayout.NORTH);

        // 2) Selected Scholarship Details Card (Read-Only Review per FR-15)
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

        idField = createReadOnlyField();
        nameField = createReadOnlyField();
        categoryField = createReadOnlyField();
        programField = createReadOnlyField();
        startDateField = createReadOnlyField();
        deadlineField = createReadOnlyField();
        statusField = createReadOnlyField();

        addTwoColumnRow(detailsCard, gbc, 0, "Scholarship ID:", idField, "Advertisement Status:", statusField);
        addTwoColumnRow(detailsCard, gbc, 1, "Scholarship Name:", nameField, "Category (BR-01):", categoryField);
        addTwoColumnRow(detailsCard, gbc, 2, "Offered Program:", programField, "Start Date:", startDateField);
        addTwoColumnRow(detailsCard, gbc, 3, "Deadline:", deadlineField, "", new JLabel(""));

        JLabel descLabel = new JLabel("Eligibility & Details:");
        descLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        detailsCard.add(descLabel, gbc);

        detailsArea = new JTextArea(4, 36);
        detailsArea.setEditable(false);
        detailsArea.setBackground(READONLY_BG);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(detailsArea);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        detailsCard.add(descScroll, gbc);

        // Action Buttons to proceed to Eligibility Check or Apply
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actionPanel.setOpaque(false);

        checkEligibilityButton = new JButton("Check Eligibility Criteria");
        checkEligibilityButton.setFont(BUTTON_FONT);
        checkEligibilityButton.setPreferredSize(new Dimension(190, 34));

        applyButton = new JButton("Apply for Scholarship");
        applyButton.setFont(BUTTON_FONT);
        applyButton.setBackground(NAVY);
        applyButton.setForeground(Color.WHITE);
        applyButton.setOpaque(true);
        applyButton.setFocusPainted(false);
        applyButton.setPreferredSize(new Dimension(180, 34));

        actionPanel.add(checkEligibilityButton);
        actionPanel.add(applyButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        detailsCard.add(actionPanel, gbc);

        JScrollPane detailsScroll = new JScrollPane(detailsCard);
        detailsScroll.setBorder(null);
        bodyPanel.add(detailsScroll, BorderLayout.CENTER);

        contentContainer.add(bodyPanel, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);

        // -------------------- EVENT HANDLERS --------------------
        viewButton.addActionListener(e -> loadAvailableScholarships());

        categoryFilterCombo.addActionListener(e -> {
            if (scholarshipsLoaded) {
                applyFiltersAndRefreshTable();
            }
        });

        programFilterCombo.addActionListener(e -> {
            if (scholarshipsLoaded) {
                applyFiltersAndRefreshTable();
            }
        });

        resetFilterButton.addActionListener(e -> {
            categoryFilterCombo.setSelectedIndex(0);
            programFilterCombo.setSelectedIndex(0);
            if (scholarshipsLoaded) {
                applyFiltersAndRefreshTable();
            }
        });

        scholarshipTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedScholarship();
            }
        });

        checkEligibilityButton.addActionListener(e -> openNextStep("Check Eligibility Criteria"));
        applyButton.addActionListener(e -> openNextStep("Submit Application"));

        setVisible(true);
    }

    // Allows running ViewAvailableScholarshipsFrame.java directly in VS Code
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewAvailableScholarshipsFrame());
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

    // UC Step 1 & 2: Retrieve advertised scholarships and handle Exception Flows A & B
    private void loadAvailableScholarships() {
        clearSelectedDetails();
        tableModel.setRowCount(0);
        displayedScholarships.clear();

        try {
            if (allScholarships == null) {
                throw new IllegalStateException("Scholarship repository unavailable");
            }
            scholarshipsLoaded = true;
            applyFiltersAndRefreshTable();
        } catch (Exception ex) {
            // Exception Flow A: System cannot retrieve scholarships
            scholarshipsLoaded = false;
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Unable to retrieve available scholarships! Would you like to check the advertisement section yourself?",
                "Retrieval Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                    this,
                    "Navigating to Advertisement Section to view scholarship notices directly."
                );
            }
        }
    }

    // UC Step 3 & Alternative Flow A: Filter by category and program (only Advertised per FR-13)
    private void applyFiltersAndRefreshTable() {
        tableModel.setRowCount(0);
        displayedScholarships.clear();
        clearSelectedDetails();

        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        String selectedProgram = (String) programFilterCombo.getSelectedItem();

        for (ScholarshipRecord s : allScholarships) {
            // FR-13: Only scholarships with status "Advertised" are visible to students
            if (!"Advertised".equalsIgnoreCase(s.getAdvertisementStatus())) {
                continue;
            }
            if (!"All Categories".equals(selectedCategory)
                && !s.getCategory().equalsIgnoreCase(selectedCategory)) {
                continue;
            }
            if (!"All Programs".equals(selectedProgram)
                && !s.getProgram().equalsIgnoreCase(selectedProgram)) {
                continue;
            }
            displayedScholarships.add(s);
            tableModel.addRow(new Object[]{
                s.getScholarshipId(),
                s.getName(),
                s.getCategory(),
                s.getProgram(),
                s.getDeadline(),
                s.getAdvertisementStatus()
            });
        }

        if (displayedScholarships.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No scholarships are available at the moment.",
                "No Matching Scholarships",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // UC Step 4 & FR-15: Display full details of the selected scholarship
    private void displaySelectedScholarship() {
        int row = scholarshipTable.getSelectedRow();
        if (row < 0 || row >= displayedScholarships.size()) {
            return;
        }

        ScholarshipRecord s = displayedScholarships.get(row);
        idField.setText(String.valueOf(s.getScholarshipId()));
        nameField.setText(s.getName());
        categoryField.setText(s.getCategory());
        programField.setText(s.getProgram());
        startDateField.setText(s.getStartDate());
        deadlineField.setText(s.getDeadline());
        statusField.setText(s.getAdvertisementStatus());
        detailsArea.setText(s.getEligibilityDetails());
    }

    private void clearSelectedDetails() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        programField.setText("");
        startDateField.setText("");
        deadlineField.setText("");
        statusField.setText("");
        detailsArea.setText("");
    }

    private void openNextStep(String targetAction) {
        int row = scholarshipTable.getSelectedRow();
        if (row < 0 || row >= displayedScholarships.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Please click 'View Scholarships' and select a scholarship first."
            );
            return;
        }
        ScholarshipRecord s = displayedScholarships.get(row);
        JOptionPane.showMessageDialog(
            this,
            "Selected Scholarship: " + s.getName() + " (" + s.getCategory() + " - " + s.getProgram() + ")\n"
            + "Proceeding to: " + targetAction
        );
    }

    private List<ScholarshipRecord> createSampleScholarships() {
        List<ScholarshipRecord> list = new ArrayList<>();
        list.add(new ScholarshipRecord(
            1001, "Need Based Scholarship BS", "Need based", "BS",
            "01/10/2026", "30/10/2026", "Advertised",
            "Eligibility: Enrolled in QAU BS program with demonstrated financial need.\n"
          + "Required Documents (PDF/DOCX): Marksheets/Degree, Utility Bills, Student ID Card, Father/Guardian Income Slip.\n"
          + "Business Rule BR-04: Students already holding an approved scholarship are not eligible."
        ));
        list.add(new ScholarshipRecord(
            1002, "Merit Based Scholarship BS", "Merit based", "BS",
            "05/10/2026", "05/11/2026", "Advertised",
            "Eligibility: Enrolled in QAU BS program with top semester CGPA (>= 3.70).\n"
          + "Required Documents (PDF/DOCX): Latest Academic Transcript/Marksheet, Student ID Card.\n"
          + "Business Rule BR-05: Only BS students may apply for this scholarship."
        ));
        list.add(new ScholarshipRecord(
            1003, "Need Based Support Grant MS", "Need based", "MS",
            "08/10/2026", "12/11/2026", "Advertised",
            "Eligibility: Enrolled in QAU MS program unable to afford semester tuition fees.\n"
          + "Required Documents (PDF/DOCX): BS Degree & Transcript, Utility Bills, ID Card, Family Income Slip.\n"
          + "Business Rule BR-05: Only MS students may apply."
        ));
        list.add(new ScholarshipRecord(
            1004, "Research Merit Fellowship PhD", "Merit based", "PhD",
            "10/10/2026", "20/11/2026", "Advertised",
            "Eligibility: Enrolled in QAU PhD program with strong research record and CGPA >= 3.75.\n"
          + "Required Documents (PDF/DOCX): MPhil/MS Transcripts, Student ID Card, Research Synopsis.\n"
          + "Business Rule BR-05: Restricted to PhD students."
        ));
        list.add(new ScholarshipRecord(
            1005, "Winter Financial Aid MPhil", "Need based", "MPhil",
            "Pending", "15/12/2026", "Not Advertised",
            "Internal draft scholarship not yet advertised by Scholarship Section (hidden from students per FR-13)."
        ));
        return list;
    }

    // Embedded model class so ViewAvailableScholarshipsFrame.java compiles with zero external files
    private static class ScholarshipRecord {
        private int scholarshipId;
        private String name;
        private String category;
        private String program;
        private String startDate;
        private String deadline;
        private String advertisementStatus;
        private String eligibilityDetails;

        public ScholarshipRecord(
            int scholarshipId, String name, String category, String program,
            String startDate, String deadline, String advertisementStatus,
            String eligibilityDetails
        ) {
            this.scholarshipId = scholarshipId;
            this.name = name;
            this.category = category;
            this.program = program;
            this.startDate = startDate;
            this.deadline = deadline;
            this.advertisementStatus = advertisementStatus;
            this.eligibilityDetails = eligibilityDetails;
        }

        public int getScholarshipId() { return scholarshipId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getProgram() { return program; }
        public String getStartDate() { return startDate; }
        public String getDeadline() { return deadline; }
        public String getAdvertisementStatus() { return advertisementStatus; }
        public String getEligibilityDetails() { return eligibilityDetails; }
    }
}