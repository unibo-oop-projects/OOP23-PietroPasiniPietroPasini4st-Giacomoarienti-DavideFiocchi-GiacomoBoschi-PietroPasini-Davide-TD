package it.unibo.towerdefense.view.savings;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import it.unibo.towerdefense.controller.savings.SavingsController;
import it.unibo.towerdefense.model.saving.Saving;

/**
 * Savings View implementation.
 *
 */
public class SavingsViewImpl implements SavingsView {

    private static final int BOTTOM_BORDER = 10;
    private final SavingsController controller;

    /**
     * Create a new instance of SavingsViewImpl.
     * @param controller the view's controller
     */
    public SavingsViewImpl(final SavingsController controller) {
        this.controller = controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPanel build(final Runnable onClose) {
        // get the list of savings
        final List<Saving> savings = this.controller.getSavings();
        // create main panel
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // create inner panel
        final JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        // if there are no savings, display a message
        if (savings.isEmpty()) {
            final JLabel noSavingsLabel = new JLabel("No savings available");
            noSavingsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            noSavingsLabel.setBorder(
                BorderFactory.createEmptyBorder(0, 0, BOTTOM_BORDER, 0)
            );
            innerPanel.add(noSavingsLabel);
        }
        // create a panel for each saving
        for (final Saving saving: savings) {
            innerPanel.add(
                new SavingsPanel(
                    saving,
                    onClose
                )
            );
        }
        // add close button
        final JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> onClose.run());
        // create scroll pane
        final JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // add components to the main panel
        panel.add(scrollPane);
        panel.add(closeButton);
        return panel;
    }

    private class SavingsPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Saving saving;
        private final Runnable onClose;

        private void onClick() {
            controller.loadSaving(this.saving);
            this.onClose.run();
        }

        public SavingsPanel(final Saving saving, final Runnable onClose) {
            this.saving = saving;
            this.onClose = onClose;
            // build the view
            this.add(new JLabel(saving.getName()));
            // create load button and add it to the panel
            final JButton loadButton = new JButton("Load");
            loadButton.addActionListener(e -> onClick());
            this.add(loadButton);
            // set horizontal alignment
            this.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            // add bottom padding
            this.setBorder(
                BorderFactory.createEmptyBorder(0, 0, BOTTOM_BORDER, 0)
            );
        }
    }
}
