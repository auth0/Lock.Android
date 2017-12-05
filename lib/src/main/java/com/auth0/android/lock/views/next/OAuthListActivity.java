package com.auth0.android.lock.views.next;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.ServiceLocator;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;
import com.auth0.android.lock.views.next.ui.OAuthAdapter;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.ArrayList;

/**
 * Created by lbalmaceda on 06/12/2017.
 */

public class OAuthListActivity extends AppCompatActivity {
    private static final String EXTRA_CONNECTIONS = "a0.extra.connections";

    public static void startFor(Context context, ArrayList<OAuthConnection> connections) {
        Intent intent = new Intent(context, OAuthListActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_CONNECTIONS, connections);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_CONNECTIONS)) {
            throw new IllegalArgumentException("This activity requires a list of OAuth connections to display");
        }
        ArrayList<OAuthConnection> connections = extras.getParcelableArrayList(EXTRA_CONNECTIONS);

        ListView list = new ListView(this);
        list.setAdapter(new OAuthAdapter(this, connections));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OAuthConnection connection = (OAuthConnection) parent.getItemAtPosition(position);
                webAuth(connection.getName());
            }
        });
        setContentView(list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Choose an Identity Provider");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void webAuth(String connectionName) {
        WebAuthProvider.init(ServiceLocator.getAccount())
                .withScheme("demo") //Fixme: Use a setting to set this value
                .withConnection(connectionName)
                .start(this, authenticationCallback);
    }


    private AuthCallback authenticationCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(final AuthenticationException exception) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OAuthListActivity.this, "Err: " + exception.getDescription(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onSuccess(@NonNull final Credentials credentials) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OAuthListActivity.this, credentials.getAccessToken(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}
