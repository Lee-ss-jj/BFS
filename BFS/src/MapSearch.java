import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MapSearch extends JFrame {

	private static Color[] COLOR_LIST = { Color.BLACK, Color.LIGHT_GRAY, Color.BLUE };
	HashMap<Integer, int[]> pointMap = new HashMap<>();
	int[][] connectArray = new int[100][100];

	class MapPanel extends JPanel {

		public MapPanel() {
			setLayout(null);

			for (var entry : pointMap.entrySet()) {
				var radio = new JRadioButton();

				radio.setBounds(entry.getValue()[0], entry.getValue()[1], 16, 16);
				radio.setOpaque(false);

				add(radio);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(2));

			for (int to = 0; to < connectArray.length; to++) {
				for (int from = 0; from < connectArray.length; from++) {
					if (connectArray[to][from] != 0) {
						int[] toXy = pointMap.get(to);
						int[] fromXy = pointMap.get(from);

						g2d.setColor(COLOR_LIST[connectArray[to][from]]);
						g.drawLine(toXy[0] + 8, toXy[1] + 8, fromXy[0] + 8, fromXy[1] + 8);
					}

				}
			}
			g2d.setStroke(new BasicStroke(1));
		}
	}

	MapSearch() {
		this.loadMap();

		setSize(1200, 700);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		add(new MapPanel());

		var uiPanel = new JPanel(null);

		uiPanel.setPreferredSize(new Dimension(200, 0));

		var pointList = new Vector<Integer>();

		for (var key : pointMap.keySet()) {
			pointList.add(key);
		}

		var cbTo = new JComboBox<Integer>(pointList);
		var cbFrom = new JComboBox<Integer>(pointList);
		var btnSearch = new JButton("길 찾기");

		cbTo.setBounds(10, 50, 100, 30);
		cbFrom.setBounds(10, 90, 100, 30);
		btnSearch.setBounds(10, 130, 100, 30);
		btnSearch.addActionListener(e -> {
			this.search((Integer) cbTo.getSelectedItem(), (Integer) cbFrom.getSelectedItem());
		});

		uiPanel.add(cbTo);
		uiPanel.add(cbFrom);
		uiPanel.add(btnSearch);

		add(uiPanel, "East");
	}

	void search(int to, int from) {
		for (int toId = 0; toId < connectArray.length; toId++) {
			for (int fromId = 0; fromId < connectArray.length; fromId++) {
				if (connectArray[toId][fromId] != 0) {
					connectArray[toId][fromId] = 1;
				}
			}
		}

		HashMap<Integer, Integer> traceMap = new HashMap<Integer, Integer>();
		Queue<Integer> q = new LinkedList<Integer>();

		traceMap.put(from, null);
		q.add(from);

		while (q.isEmpty() == false) {
			int currentPoint = q.poll();

			if (currentPoint == to) {
				break;
			}

			for (int i = 1; i < connectArray.length; i++) {
				if (connectArray[currentPoint][i] != 0 && traceMap.containsKey(i) == false) {
					q.add(i);
					traceMap.put(i, currentPoint);
				}
			}
		}

		if (traceMap.containsKey(to) == false) {
			JOptionPane.showMessageDialog(this, "도착 못함");
			return;
		}

		int curPoint = to;

		while (traceMap.get(curPoint) != null) {
			connectArray[curPoint][traceMap.get(curPoint)] = 2;
			connectArray[traceMap.get(curPoint)][curPoint] = 2;
			curPoint = traceMap.get(curPoint);
		}

		this.repaint();
	}

	void loadMap() {

		try {
			var lines = Files.readAllLines(Paths.get("./point.txt"));

			for (int i = 1; i < lines.size(); i++) {
				String[] split = lines.get(i).split("\t");
				int id = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);

				pointMap.put(id, new int[] { x, y });
			}

			var connectLines = Files.readAllLines(Paths.get("./connect.txt"));

			for (int i = 1; i < connectLines.size(); i++) {
				String[] split = connectLines.get(i).split("\t");
				int to = Integer.parseInt(split[0]);
				int from = Integer.parseInt(split[1]);

				connectArray[to][from] = 1;
				connectArray[from][to] = 1;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MapSearch().setVisible(true);
	}
}
