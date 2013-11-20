package com.example.android.input.multitouch.basicMultitouch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.input.multitouch.basicMultitouch.R;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

public class ScanActivity extends Activity
{
    /** Called when the activity is first created. */
    Button    b1;
    Button    btnTouch;
    ImageView playerImg;
    TextView  headline;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        headline = ( TextView ) findViewById( R.id.txt_head);
        playerImg = ( ImageView ) findViewById( R.id.img_player );
        b1 = ( Button ) findViewById( R.id.btn_scan );
        btnTouch = ( Button ) findViewById( R.id.btn_touch );
        b1.setOnClickListener( new OnClickListener()
        {

            public void onClick( View arg0 )
            {
                // TODO Auto-generated method stub
                // Intent intent = new Intent( ScanActivity.this,
                // CaptureActivity.class );
                // // Intent intent = new
                // // Intent("com.google.zxing.client.android.SCAN");
                // intent.putExtra( "SCAN_MODE", "QR_CODE_MODE" );

                Intent intent = new Intent( ScanActivity.this, CaptureActivity.class );
                intent.setAction( Intents.Scan.ACTION );
                intent.putExtra( "com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE" );
                startActivityForResult( intent, 0 );

            }
        } );

        btnTouch.setOnClickListener( new OnClickListener()
        {

            @Override
            public void onClick( View v )
            {
                Intent intent = new Intent( ScanActivity.this, FirstGameActivity.class );
                startActivity( intent );

            }
        } );

    }

    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
        if ( requestCode == 0 )
        {
            if ( resultCode == RESULT_OK )
            {
                String contents = intent.getStringExtra( "SCAN_RESULT" );
                String format = intent.getStringExtra( "SCAN_RESULT_FORMAT" );
                Log.i( "xZing", "contents: " + contents + " format: " + format );
                // Handle successful scan
                showPlayer( contents, format );
            }
            else if ( resultCode == RESULT_CANCELED )
            { // Handle cancel
                Log.i( "xZing", "Cancelled" );
            }
        }
    }

    private void showPlayer( String contents, String format )
    {
        if( contents.toLowerCase().contains( "monster" ))
        {
            playerImg.setImageResource( R.drawable.gantor );
            headline.setText( "Welcome Gantor!" );
        }
        else if (contents.toLowerCase().contains( "leo" ))
        {
            playerImg.setImageResource( R.drawable.leo );
            headline.setText( "Welcome Leo!" );
        }
        else if (contents.toLowerCase().contains( "stefan" ))
        {
            playerImg.setImageResource( R.drawable.stef );
            headline.setText( "Welcome Stefko!" );
        }
        else if (contents.toLowerCase().contains( "cat" ))
        {
            playerImg.setImageResource( R.drawable.cat );
            headline.setText( "Welcome Catzor!" );
        }
        else
        {
            playerImg.setImageResource( R.drawable.yoda );
            headline.setText( "Welcome Yoda!" );
        }
        playerImg.setVisibility( View.VISIBLE );
    }

    // public void onActivityResult( int requestCode, int resultCode, Intent
    // intent )
    // {
    // if ( requestCode == 0 )
    // {
    // if ( resultCode == 1 )
    // {
    // // Handle successful scan
    // String capturedQrValue = intent.getStringExtra( "RESULT" );
    // // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
    // Toast.makeText(
    // ScanActivity.this, "Scan Result:" + capturedQrValue, Toast.LENGTH_SHORT )
    // .show();
    //
    // }
    // else if ( resultCode == RESULT_CANCELED )
    // {
    // // Handle cancel
    // }
    // }
    // else
    // {
    //
    // }
    // }
}
