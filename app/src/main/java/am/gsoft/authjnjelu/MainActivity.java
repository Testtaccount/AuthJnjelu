package am.gsoft.authjnjelu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookButtonBase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.q_municate_auth_service.QMAuthService;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.LoginType;
import com.quickblox.q_municate_core.qb.commands.rest.QBSignUpCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import io.fabric.sdk.android.Fabric;
import java.util.Arrays;
import java.util.List;
import rx.Observer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "testt";
  protected static final String STARTED_LOGIN_TYPE = "started_login_type";

  protected FacebookHelper facebookHelper;
  private FirebaseAuthHelper firebaseAuthHelper;
  private FirebaseAuthCallback firebaseAuthCallback;
  protected LoginType loginType = LoginType.EMAIL;
  protected Resources resources;
  protected FailAction failAction;
  private ServiceManager serviceManager;
  protected SharedHelper appSharedHelper;
  private Button fbpaBtn;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    setContentView(R.layout.activity_main);
    initFields(savedInstanceState);
    AppSession.load();
    fbpaBtn= (Button) findViewById(R.id.btn_fbpa);
    fbpaBtn.setOnClickListener(this);
    processPushIntent();

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(STARTED_LOGIN_TYPE, loginType);
  }

  private void processPushIntent() {
    boolean openPushDialog = getIntent()
        .getBooleanExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, false);
    CoreSharedHelper.getInstance().saveNeedToOpenDialog(openPushDialog);
  }

  private void initFields(Bundle savedInstanceState) {
    resources = getResources();
    appSharedHelper = (SharedHelper) App.getInstance().getAppSharedHelper();

    if (savedInstanceState != null && savedInstanceState.containsKey(STARTED_LOGIN_TYPE)) {
      loginType = (LoginType) savedInstanceState.getSerializable(STARTED_LOGIN_TYPE);
    }
    facebookHelper = new FacebookHelper(this);
    firebaseAuthHelper = new FirebaseAuthHelper();
    firebaseAuthCallback = new FirebaseAuthCallback();
    failAction = new FailAction();
    serviceManager = ServiceManager.getInstance();
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_fbpa:
        loginType = LoginType.FIREBASE_PHONE;
        startSocialLogin();
        break;
    }

  }

  protected void startSocialLogin() {

      loginWithSocial();

  }

  public static void start(Context context, Intent intent) {
    context.startActivity(intent);
  }

  private void loginWithSocial() {
    appSharedHelper.saveFirstAuth(true);
    appSharedHelper.saveSavedRememberMe(true);

      firebaseAuthHelper.loginByPhone(MainActivity.this);

  }
//  public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
////    showProgress();
//    // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
//    if (requestCode == RC_SIGN_IN) {
//      IdpResponse response = IdpResponse.fromResultIntent(data);
//      // Successfully signed in
//      if (resultCode == ResultCodes.OK) {
////    by meeeeee    AppSession.startSession(LoginType.TWITTER_DIGITS, qbUser, qbToken);
//
//        final String projectId = "am.gsoft.authjnjelu";
////        String accessToken = currentUser.getPhoneNumber();
//
////        QBUser user =  QBUsers.signInUsingFirebase(projectId,idToken.toString()).performAsync(
////            new QBEntityCallback<QBUser>() {
////              @Override
////              public void onSuccess(QBUser qbUser, Bundle bundle) {
////
////              }
////
////              @Override
////              public void onError(QBResponseException e) {
////
////              }
////            });
////        Log.d("testt", currentUser.getUid());
//
////        Thread thread = new Thread(new Runnable() {
////
////          @Override
////          public void run() {
////            try  {
////              //Your code goes here
////            } catch (Exception e) {
////              e.printStackTrace();
////            }
////          }
////        });
////
////        thread.start();
//
////        DataManager.getInstance().clearAllTables();
////        AppSession.getSession().closeAndClear();
////        QBUser qbUser=new QBUser(currentUser.getUid());
//
////        QBSignUpCommand.start(this,qbUser,null);
////          qmAuthService.signUpLogin(qbUser);
//
////        QBUsers.signInUsingFirebase()
//
//
////        QBUsers.signInUsingFirebase(projectId,currentUser.getUid());
//
////        QBAuth.createSessionUsingSocialProvider(QBProvider.FIREBASE_PHONE,currentUser.getUid(),projectId).performAsync(
////            new QBEntityCallback<QBSession>() {
////              @Override
////              public void onSuccess(QBSession qbSession, Bundle bundle) {
////                Log.d("testt", "aaaaaaaaaaaaaa");
////
////              }
////
////              @Override
////              public void onError(QBResponseException e) {
////                Log.d("testt", "error");
////
////              }
////            });
//
////        QBUsers.signInUsingFirebase(projectId, accessToken).performAsync( new QBEntityCallback<QBUser>() {
////          @Override
////          public void onSuccess(QBUser user, Bundle args) {
////            Log.d("testt", "aaaaaaaaaaaaaa");
////            startActivity(new Intent(MainActivity.this, HomeActivity.class));
////            finish();
////          }
////
////          @Override
////          public void onError(QBResponseException error) {
////            Log.d("testt", "error");
////          }
////        });
//
//        return;
//      } else {
//        // Sign in failed
//        if (response == null) {
//          // User pressed back button
//          Log.e("Login", "Login canceled by User");
////          hideProgress();
//          return;
//        }
//        if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//          Log.e("Login", "No Internet Connection");
////          hideProgress();
//          return;
//        }
//        if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//          Log.e("Login", "Unknown Error");
////          hideProgress();
//          return;
//        }
//      }
//      Log.e("Login", "Unknown sign in response");
//    }
//  }

  public class FailAction implements Command {

    @Override
    public void execute(Bundle bundle) {
      Exception e = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
      ErrorUtils.showError(MainActivity.this, e);
      onFailAction(bundle.getString(QBServiceConsts.COMMAND_ACTION));
    }
  }

  protected void onFailAction(String action) {
  }

  private Observer<QBUser> socialLoginObserver = new Observer<QBUser>() {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
      Log.d(TAG, "onError " + e.getMessage());
      AuthUtils.parseExceptionMessage(MainActivity.this, e.getMessage());
    }

    @Override
    public void onNext(QBUser qbUser) {
      performLoginSuccessAction(qbUser);
    }
  };


  private void performLoginSuccessAction(QBUser user) {
    startMainActivity(user);
    startActivity(new Intent(MainActivity.this, HomeActivity.class));
    finish();
  }

  protected void startMainActivity(QBUser user) {
    AppSession.getSession().updateUser(user);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult, result code = " + requestCode);
    if (requestCode == FirebaseAuthHelper.RC_SIGN_IN){
      onReceiveFirebaseAuthResult(resultCode, data);
    }
    facebookHelper.onActivityResult(requestCode, resultCode, data);
  }

  private void onReceiveFirebaseAuthResult(int resultCode, Intent data) {
    IdpResponse response = IdpResponse.fromResultIntent(data);

    // Successfully signed in
    if (resultCode == RESULT_OK) {
      FirebaseAuthHelper.getIdTokenForCurrentUser(firebaseAuthCallback);
      return;
    } else {
      //Sign in failed
      if (response == null) {
        // User pressed back button
        Log.i(TAG, "BACK button pressed");
        return;
      }

      if (response.getErrorCode() == ErrorCodes.NO_NETWORK || response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
        Toast.makeText(this,"Something went wrong. Please, verify your internet connection and try again", Toast.LENGTH_LONG).show();
        return;
      }
    }
  }


  private class FirebaseAuthCallback implements FirebaseAuthHelper.RequestFirebaseIdTokenCallback {

    @Override
    public void onSuccess(String authToken) {
      Log.d(TAG, "FirebaseAuthCallback onSuccess()");
      serviceManager
          .login(QBProvider.FIREBASE_PHONE, authToken, StringObfuscator.getFirebaseAuthProjectId())
          .subscribe(socialLoginObserver);
    }

    @Override
    public void onError(Exception e) {
      Log.d(TAG, "FirebaseAuthCallback onError()");
    }
  }

}
