package de.hhn.aib.swlab.wise1920.group06.exercise3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.hhn.aib.swlab.wise1920.group06.exercise3.MyUnoGame;
import de.hhn.aib.swlab.wise1920.group06.exercise3.activities.GameActivity;
import de.hhn.aib.swlab.wise1920.group06.exercise3.activities.LobbyActivity;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MyUnoGame(), config);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(AndroidLauncher.this)
				.setTitle("Abmelden")
				.setMessage("Bist du dir sicher, dass du das Spiel verlassen willst?")

				// Specifying a listener allows you to take an action before dismissing the dialog.
				// The dialog is automatically dismissed when a dialog button is clicked.
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Continue with delete operation
						Intent intent = new Intent(AndroidLauncher.this, LobbyActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();

						//Send info to backend

					}
				})

				// A null listener allows the button to dismiss the dialog and take no further action.
				.setNegativeButton(android.R.string.no, null)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();

	}
}
