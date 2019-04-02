

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Player 
{
	
	private Card card1,card2;
	private boolean teamRed;

	private ArrayList<Piece> disciples = new ArrayList<Piece>();
	private Piece master;
	
	public Player(boolean teamRed) 
	{
		
		this.teamRed = teamRed;
		if(teamRed)
		{
			for(int i = 0; i < 5;i++) 
			{
				if(i > 2)
					disciples.add(new Piece(teamRed,new Position(4,i),false,i-1));
				else if(i < 2)
					disciples.add(new Piece(teamRed,new Position(4,i),false,i));
				else
					master = new Piece(teamRed,new Position(4,i),true,4);
			}
		} else
		{
			for(int i = 0; i < 5;i++) 
			{
				if(i > 2)
					disciples.add(new Piece(teamRed,new Position(0,i),false,i-1));
				else if(i < 2)
					disciples.add(new Piece(teamRed,new Position(0,i),false,i));
				else
					master = new Piece(teamRed,new Position(0,i),true,4);
			}
		}
	}
	
	public boolean isTeamRed() {
		return teamRed;
	}

	public void setTeamRed(boolean teamRed) {
		this.teamRed = teamRed;
	}

	public void drawPieces(Graphics g) {
		for(int i = 0; i < disciples.size();i++) {
			if(!disciples.get(i).getDead()) {
				if(teamRed) {
					try {
						final BufferedImage piece = ImageIO.read(Onitama.class.getResourceAsStream("/resources/red disciple.png"));
						//final BufferedImage piece = ImageIO.read(new File("src\\resources\\red disciple.png"));
						g.drawImage(piece, disciples.get(i).getPosition().getXCoord(), disciples.get(i).getPosition().getYCoord(), null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						final BufferedImage piece = ImageIO.read(Onitama.class.getResourceAsStream("/resources/blue disciple.png"));
						//final BufferedImage piece = ImageIO.read(new File("src\\resources\\blue disciple.png"));
						g.drawImage(piece, disciples.get(i).getPosition().getXCoord(), disciples.get(i).getPosition().getYCoord(), null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(!master.getDead()) {
			if(teamRed) {
				try {
					final BufferedImage piece = ImageIO.read(Onitama.class.getResourceAsStream("/resources/red master.png"));
					//final BufferedImage piece = ImageIO.read(new File("src\\resources\\red master.png"));
					g.drawImage(piece, master.getPosition().getXCoord(), master.getPosition().getYCoord(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					final BufferedImage piece = ImageIO.read(Onitama.class.getResourceAsStream("/resources/blue master.png"));
					//final BufferedImage piece = ImageIO.read(new File("src\\resources\\blue master.png"));
					g.drawImage(piece, master.getPosition().getXCoord(), master.getPosition().getYCoord(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String toString() {
		return "teamRed: " + teamRed + ", card1: " + card1 + 
				", card2: " + card2 + ", #disc: " + disciples.size() + 
				", master: " + master.getMaster();
	}
	
	public Piece getPiece(int ID)
	{
		if(ID == 4)
			return master;
		for(int i = 0; i < disciples.size();i++)
		{
			if(disciples.get(i).getID()==ID)
				return disciples.get(i);
		}
		return null;
	}
	
	public Piece getPiece(Position pos) 
	{
		for(int i = 0; i < disciples.size();i++) {
			if(disciples.get(i).getPosition().equals(pos)) {
				if(!disciples.get(i).getDead())
					return disciples.get(i);
			}
		}
		
		if(master.getPosition().equals(pos))
			return master;
		return null;
	}

	public Card getCard1() {
		return card1;
	}
	
	public ArrayList<Piece> getDisciples() {
		return disciples;
	}
	
	public Piece getMaster() {
		return master;
	}

	public void setCard1(Card card1) {
		this.card1 = new Card(card1.getCardID());
	}

	public Card getCard2() {
		return card2;
	}

	public void setCard2(Card card2) {
		this.card2 = new Card(card2.getCardID());
	}
	
	

}