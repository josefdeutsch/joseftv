package com.example.android.tvleanback.net;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.android.tvleanback.R;
import com.example.android.tvleanback.data.FetchVideoService;
import com.example.android.tvleanback.ui.LeanbackActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends LeanbackActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public AlertDialog mDialog;
    public static final int RC_SIGN_IN = 9001;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";

    public void setupFirebaseAuth() {

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();//
                    DatabaseReference myRef = database.getReference("users").child(user.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                           /** String data = "";
                            try {
                                data = createJson();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }**/

                            String value = dataSnapshot.child("png").getValue().toString();
                            Log.d(TAG, "onDataChange: "+value);
                            Intent serviceIntent = new Intent(getApplicationContext(), FetchVideoService.class);
                            serviceIntent.putExtra("data",value);
                            getApplication().startService(serviceIntent);

                            Log.d(TAG, "Value is: " + value);
                        }

                        private String createJson() throws JSONException {

                            ArrayList<String> goldPng = supplyArrayGoldPng(new ArrayList());
                            ArrayList<String> goldMp4 = supplyArrayGoldMp4(new ArrayList());

                            JSONObject jsonObject = new JSONObject();
                            JSONArray googlevideos = new JSONArray();
                            JSONObject data = new JSONObject();
                            JSONArray sources = new JSONArray();

                            for (int i = 0; i <= goldMp4.size() - 1; i++) {

                                JSONObject sum = new JSONObject();
                                sum.put("description", "LoremIpsum...");
                                JSONArray path = new JSONArray();
                                path.put(goldMp4.get(i));
                                sum.put("sources", path);
                                sum.put("card", goldPng.get(i));
                                sum.put("background", goldPng.get(i));
                                sum.put("title", "material");
                                sum.put("studio", "Google+");
                                sources.put(sum);

                            }
                            data.put("category", "Google+");
                            data.put("videos", sources);
                            googlevideos.put(data);
                            jsonObject.put("googlevideos", googlevideos);

                            return jsonObject.toString();
                        }

                        private ArrayList<String> supplyArrayGoldPng(ArrayList arraylist) {

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00010621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00020621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00030621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00040621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00050621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00060621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00070621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00080621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00090621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00100621.png");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00110621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00120621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00130621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00140621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00150621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00160621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00170621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00180621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00190621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00200621.png");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00210621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00220621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00230621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00240621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00250621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00260621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00270621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00280621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00290621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00300621.png");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00310621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00320621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00330621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00340621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00350621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00360621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00370621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00380621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00390621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00400621.png");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00410621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00420621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00430621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00440621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00450621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00460621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00470621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00480621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00490621.png");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/00500621.png");

                            return arraylist;

                        }

                        private ArrayList<String> supplyArrayGoldMp4(ArrayList arraylist) {

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0001.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0002.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0003.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0004.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0005.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0006.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0007.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0008.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0009.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0010.mp4");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0011.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0012.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0013.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0014.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0015.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0016.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0017.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0018.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0019.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0020.mp4");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0021.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0022.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0023.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0024.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0025.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0026.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0027.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0028.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0029.mp4");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0030.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0031.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0032.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0033.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0034.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0035.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0036.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0037.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0038.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0039.mp4");

                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0040.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0041.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0042.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0043.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0044.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0045.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0046.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0047.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0048.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0049.mp4");
                            arraylist.add("http://joseph3d.com/wp-content/uploads/2019/06/g0050.mp4");

                            return arraylist;

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
    }

    public void buildGoogleApiClient(GoogleSignInOptions gso) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @NotNull
    public GoogleSignInOptions setupGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //use alertDialog...
        //showProgressDialog(this);

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);

                } else {

                }

                //hideProgressDialog();

            }
        });
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.logout);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                /**
                                 *
                                 *
                                 *
                                 */
                            }
                        }
                );
            }
        });

        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.show();
    }

    public void updateUI(FirebaseUser user) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //  Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(R.layout.progressdialog);
        mDialog = builder.create();
        mDialog.show();
    }

    public void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
