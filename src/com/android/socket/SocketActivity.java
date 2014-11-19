package com.android.socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SocketActivity extends Activity {

	private Button btn_send = null;
	private Button btn_recevie = null;
	private TextView txt_output = null;
	private EditText txt_input = null;
    private Handler mHandler = new myHandle();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btn_send = (Button) findViewById(R.id.btn_send);
		btn_recevie = (Button) findViewById(R.id.btn_receive);

		txt_input = (EditText) findViewById(R.id.txt_input);
		txt_output = (TextView) findViewById(R.id.txt_output);

		btn_send.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread(SendThread).start();
			}
		});

		btn_recevie.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "test", 1).show();
				new Thread(RecevieThread).start();
			}
		});
	}

	public class myHandle extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Toast.makeText(getApplicationContext(), "test", 1).show();
			
			System.out.println("Address: " );
			switch (msg.what) {
			case 1:
				
				appendOutput((String)msg.obj);
				
				break;

			default:
				break;
			}
			
			
		}
				
	}
	
	
	Runnable RecevieThread = new Runnable() {
		@Override
		public void run() {
			ServerSocket server = null;
			try {
				server = new ServerSocket(5050);
				while (true) {
					System.out.println("I'm listening...");
					final Socket socket = server.accept();

					new Thread(new Runnable() {
						@Override
						public void run() {
							System.out.println("Address: " + socket.getLocalAddress() + ":" + socket.getLocalPort());

							InputStream input;
							try {
								input = socket.getInputStream();

								byte buffer[] = new byte[1024 * 4];
								int tmp = 0;
								
								String result = "";
								while ((tmp = input.read(buffer)) != -1) {
									
									String content = new String(buffer, 0, tmp);
									System.out.println("receive: " + content);
									result+=content;
									
									Message resMessage = mHandler.obtainMessage();
									resMessage.what =1;
									resMessage.obj =(String)content;
									mHandler.sendMessage(resMessage);
									
									System.out.println("before message!" );
									
								}
                    
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();

				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					server.close();
					System.out.println("server closed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	Runnable SendThread = new Runnable() {
		public void run() {
			try {
				Socket socket = new Socket("192.168.0.102", 5050);

				OutputStream output = socket.getOutputStream();
				byte buffer[] = new byte[1024 * 4];
				int tmp = 0;

				String str = SocketActivity.this.getTxt_input().getText()
						.toString();
				InputStream input = new ByteArrayInputStream(str.getBytes());

				while ((tmp = input.read(buffer)) != -1) {
				
					output.write(buffer, 0, tmp);
				}
				output.flush();
				System.out.println("Message has been sent");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public Button getBtn_send() {
		return btn_send;
	}

	public void setBtn_send(Button btn_send) {
		this.btn_send = btn_send;
	}

	public Button getBtn_recevie() {
		return btn_recevie;
	}

	public void setBtn_recevie(Button btn_recevie) {
		this.btn_recevie = btn_recevie;
	}

	public TextView getTxt_output() {
		return txt_output;
	}

	public void setTxt_output(TextView txt_output) {
		this.txt_output = txt_output;
	}

	public EditText getTxt_input() {
		return txt_input;
	}

	public void setTxt_input(EditText txt_input) {
		this.txt_input = txt_input;
	}

	public void appendOutput(String str) {
		this.txt_output.setText(this.txt_output.getText() + "\n" + str);
	}
}