package loggerForms.videocontrol.swing.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamPanel;
import loggerForms.videocontrol.StatusMessage;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.VideoObserver;
import loggerForms.videocontrol.protocols.VideoProtocol;

public class DeviceSideStrip extends PamPanel implements VideoObserver {

	private VideoProtocol videoProtocol;
	
/**
	 * @return the videoProtocol
	 */
	public VideoProtocol getVideoProtocol() {
		return videoProtocol;
	}

	//	private JButton recButton, stopButton;
	private StateButton stateButton;
	private JTextField recStatus;

	private VideoControl videoControl;

	private StatusMessage currentStatus;
	
	/**
	 * @param videoProtocol
	 */
	public DeviceSideStrip(VideoControl videoControl, VideoProtocol videoProtocol) {
		super();
		this.videoControl = videoControl;
		this.videoProtocol = videoProtocol;
		stateButton = new StateButton(null, 16);
		stateButton.addMouseListener(new StateMouse());
		recStatus = new JTextField(12);
		recStatus.setEditable(false);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		c.ipadx = 2;
		c.ipady = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridwidth = 2;
		add(new JLabel(videoProtocol.getDeviceParameters().name, JLabel.LEFT), c);
		c.gridy++;
		c.gridwidth = 1;
		add(stateButton, c);
		c.gridx++;
		add(recStatus, c);
		
		String tip = videoProtocol.getProtocolProvider().getName();
		this.setToolTipText(tip);
		
		// don't do this, because it causes a concurrent mod exception while the list is creating .
//		videoControl.addObserver(this);
//		recButton = new JButton()
	}
	
	private class StateMouse extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (stateButton.recording == false) {
				videoProtocol.startRecording();
			}
			else {
				videoProtocol.stopRecording();
			}
//			stateButton.recording = !stateButton.recording;
//			stateButton.repaint();
		}
		
	}
	
	/**
	 * Simple icon for record or stopped. 
	 */
	private class StateButton extends PamPanel {
		
		private int size;
		
		private PamSymbol record, stop;
		
		private boolean recording = false;

		private String platformName;

		public StateButton(String platformName, int size) {
			this.platformName = platformName;
			this.size = size;
			record = new PamSymbol(PamSymbolType.SYMBOL_CIRCLE, size, size, true, Color.RED, Color.RED);
			stop = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, size-2, size-2, true, Color.BLACK, Color.BLACK);
//			addMouseListener(new StateMouse(platformName));
			setRecordng(false);
			setToolTip();
		}

		private void setToolTip() {
			setToolTipText(makeToolTip());
		}

		public void setRecordng(boolean recording) {
			if (this.recording != recording) {
				this.recording = recording;
				setToolTip();
				repaint();
			}
		}
		
		private String makeToolTip() {
			int recDur = 0;//loggerAudioControl.getLoggerAudioSettings().recordSeconds;
			if (recording == false) {
				return String.format("Click to record for %d seconds", recDur);
			}
			else {
				return String.format("Click to extend recording to %d seconds", recDur);
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(size, size);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int h = getHeight();
			int w = getWidth();
			int y = h/2;
			int x = w/2;
			PamSymbol s = recording ? record : stop;
			int border = recording ? 0 : 1;
			int sz = Math.min(h, w)-border*2;
			s.setHeight(sz);
			s.setWidth(sz);
			s.draw(g, new Point(x,y));
		}
		
	}

	@Override
	public void configurationChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChange(VideoProtocol videoProtocol, StatusMessage statusMessage) {
		if (this.videoProtocol != videoProtocol) {
			return;
		}
		this.currentStatus = statusMessage;
		switch (currentStatus.getRecordState()) {
		case ERROR:
			recStatus.setText("Error");
			recStatus.setToolTipText(currentStatus.getMessage());
			break;
		case IDLE:
			recStatus.setText(currentStatus.getRecordState().toString());
			recStatus.setToolTipText(null);
			break;
		case RECORDING:
			recStatus.setText(String.format("Rec. %ds remaining", currentStatus.getRemaining()));
			recStatus.setToolTipText(null);
			break;
		default:
			break;
		
		}
		stateButton.repaint();
	}
	
}
