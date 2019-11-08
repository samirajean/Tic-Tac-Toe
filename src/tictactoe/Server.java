package tictactoe;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class Server extends Application implements Constants {

	private int sessionNo = 1;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		TextArea T = new TextArea();
		
		Scene s = new Scene(new ScrollPane(T), 450, 200);
		primaryStage.setTitle("Server");
		primaryStage.setScene(s);
		primaryStage.show();
		
		new Thread( () -> {
			
			try
			{
			
			ServerSocket Ssocket = new ServerSocket(8000);
			Platform.runLater(() -> T.appendText(new Date()  + ": Server started at socket 8000\n"));
			
			while(true)
			{
				Platform.runLater(()-> T.appendText(new Date() + ": Wait for players to join the sesssion"));
				
				Socket p1 = Ssocket.accept();
				
				Platform.runLater(()-> { T.appendText(new Date() + "Player 1 joind thesession" + sessionNo + '\n');
				T.appendText("Player1's IP Address" + p1.getInetAddress().getHostAddress() + '\n');
				
				});
				
				new DataOutputStream(p1.getOutputStream()).writeInt(player1);
				
				Socket p2 = Ssocket.accept();
				
				Platform.runLater(()-> { T.appendText(new Date() + "Player 2 joind thesession" + sessionNo + "\n");
				T.appendText("Player2's IP address" + p2.getInetAddress().getHostAddress());
				});
	
				new DataOutputStream(p2.getOutputStream()).writeInt(player2);
				
				Platform.runLater(() -> T.appendText(new Date() + ": Start a thread for sessiob" + sessionNo++ + '\n'));
				
				new Thread(new Handle(p1, p2)).start();
			}
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	
	
		class Handle implements Runnable, Constants{
			
			 private Socket p1;
			 private Socket p2;
			 
			 private char[][] cell = new char [3][3];
			 
			 private DataInputStream fromp1;
			 private DataOutputStream topl1;
			 
			 private DataInputStream fromp2;
			 private DataOutputStream top2;
			 
			 private boolean contplaying = true;
			 
			 public Handle(Socket p1, Socket p2)
			 {
			 this.p1 = p1;
			 this.p2 = p2;
			 
			 for(int i = 0; i<3; i++)
				 for(int j= 0; j<3; j++)
					 cell[i][j] = ' ';
			 }

			@Override
			public void run() {
				try {
					DataInputStream fromp1 = new DataInputStream(p1.getInputStream());
					DataOutputStream top1 = new DataOutputStream(p1.getOutputStream());
					DataInputStream fromp2 = new DataInputStream(p2.getInputStream());
					DataOutputStream top2 = new DataOutputStream(p2.getOutputStream());
					
					//top1.writeBytes("YOUR TURN");
					
					while(true)
					{
						int row = fromp1.readInt();
						int col = fromp1.readInt();
						cell[row][col] = 'X';
						
						if(isWon('X'))
						{
							top1.writeBytes(player1Won);
							top2.writeBytes(player1Won);
							sendMove(top2, row, col);
							break;
						}
						
						else if (isFull())
						{
							top1.writeBytes(Draw);
							top2.writeBytes(Draw);
							sendMove(top2, row, col);
							break;
						}
						
						else
						{
							top1.writeBytes(Cont);
							sendMove(top2, row, col);
						}
						
						row = fromp2.readInt();
						col = fromp2.readInt();
						cell[row][col] = 'O';
						
						if(isWon('O'))
						{
							top1.writeBytes(player2Won);
							top2.writeBytes(player2Won);
							sendMove(top1, row, col);
							break;
						}
						
						else
						{
							top1.writeBytes(Cont);
							sendMove(top2, row, col);
							break;
						}
						
					}
				}
			
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
				
			}

			private boolean isFull() 
			{
				for(int i = 0; i<3; i++)
					for(int j = 0; j<3; j++)
						if(cell[i][j]==' ')
							return false;
				return true;
			}

			private boolean isWon(char c) 
			{
				for(int i = 0; i<3; i++)
					if(cell[i][0] == c && cell[i][1] == c && cell[i][2] == c )
					{
						return true;
					}
						
				for(int j = 0; j<3; j++)
					if(cell[0][j] == c && cell[1][j] == c && cell[2][j] == c )
					{
						return true;
					}
				
				if(cell[0][0] == c && cell[1][1] == c && cell[2][2] == c )
				{
					return true;
				}
				
				if(cell[0][2] == c && cell[1][1] == c && cell[2][0] == c )
				{
					return true;
				}
				
				else 
					return false;
				
					
			}

			private void sendMove(DataOutputStream out, int row, int col) throws IOException 
			{
				out.writeInt(row);
				out.writeInt(col);
				
			}
		}
}


