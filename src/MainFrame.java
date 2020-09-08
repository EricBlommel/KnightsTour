import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;

public class MainFrame extends JFrame{
	
	private int width, height;
	private int x,y;
	
	private DrawingPanel panel;

	private JToolBar toolBar = new JToolBar("Tools");
	private JCheckBox backtracking = new JCheckBox("Backtracking");
	private SpinnerModel valueX = new SpinnerNumberModel(8,3,20,1);
	private JSpinner sizeX = new JSpinner(valueX);
	private SpinnerModel valueY = new SpinnerNumberModel(8,3,20,1);
	private JSpinner sizeY = new JSpinner(valueY);
	private JLabel times = new JLabel("x");
	private JButton resize = new JButton("resize");
	private JRadioButton colorBlack = new JRadioButton("Black");
	private JRadioButton colorWhite = new JRadioButton("White");
	private ButtonGroup colorGroup = new ButtonGroup();

	/* Main */
	public static void main(String[] args){
		MainFrame frame = new MainFrame();
	}

	/* Konstruktor */
	public MainFrame(){
		super("Knight's Tour");
		x = 8;
		y = 8;
		width = 800;
		height = width / x * y;

		config();
		
		resize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getSource()==resize){
					panel.reset();
					x = (Integer) sizeX.getValue();
					y = (Integer) sizeY.getValue();
					panel.setSizeX(x);
					panel.setSizeY(y);
					panel.setWidth(getWidth());
					panel.setHeight(getWidth() / x * y);
					setSize(getWidth(), getWidth() / x * y +60);
					panel.repaint();
				}
			}
		});

		// Damit Fenster richtige groesse hat
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				setSize(getWidth(),getWidth() / x * y + 60);
				panel.setWidth(getWidth());
				panel.setHeight(getWidth() / x * y);
				panel.repaint();
			}
		});

		// Platzieren des Springers mit Maus
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				panel.reset();
				panel.setBacktracking(backtracking.isSelected());
				for (int i = 0; i<x; i++){
					for (int j = 0; j<y; j++){
						if (getWidth()/x*i < e.getX() && e.getX() < getWidth()/x*(i+1) && (getWidth()/y*j)+22 < e.getY() && e.getY() < (getWidth()/y*(j+1))+22){
							//System.out.println(i + " " + j);
							if(!panel.jump(i,j)){
								JOptionPane.showMessageDialog(null, "There is no solution", "Knight's Tour", 1);
								panel.reset();
								panel.repaint();
							}else{
								if(colorBlack.isSelected()){
									panel.loadImage("/img/black.png");
								}else{
									panel.loadImage("/img/white.png");
								}
								panel.startTour();
							}
							panel.repaint();
						}
					}
				}
			}
		});
	}
	
	public void config(){
		setLayout(null);
		setSize(width, height+60);
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		panel = new DrawingPanel(width, height, x, y);
		panel.setLayout(new BorderLayout());

		toolBar.setFloatable(false);
		toolBar.setLayout(new FlowLayout());
		
		colorGroup.add(colorBlack);
		colorGroup.add(colorWhite);
		colorBlack.setSelected(true);

		toolBar.add(backtracking);
		toolBar.add(new JSeparator(SwingConstants.VERTICAL));
		toolBar.add(sizeX);
		toolBar.add(times);
		toolBar.add(sizeY);
		toolBar.add(resize);
		toolBar.add(new JSeparator(SwingConstants.VERTICAL));
		toolBar.add(new JLabel("Knight Color:"));
		toolBar.add(colorBlack);
		toolBar.add(colorWhite);

		setContentPane(panel);
		add(toolBar, BorderLayout.SOUTH);

		setVisible(true);
	}
}

