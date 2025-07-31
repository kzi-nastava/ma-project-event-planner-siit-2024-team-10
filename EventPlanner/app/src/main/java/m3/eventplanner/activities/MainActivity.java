package m3.eventplanner.activities;

import static android.view.View.GONE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetNotificationDTO;
import m3.eventplanner.utils.NotificationWebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new TokenManager(this).clearToken();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.topAppBar);
        logoutButton=findViewById(R.id.logout_button);


        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(false);
        }

        topLevelDestinations.add(R.id.loginFragment);
        topLevelDestinations.add(R.id.homeScreenFragment);
        topLevelDestinations.add(R.id.manageOfferingsFragment);
        topLevelDestinations.add(R.id.notificationFragment);
        topLevelDestinations.add(R.id.eventTypesFragment);
        topLevelDestinations.add(R.id.createProductFragment);
        topLevelDestinations.add((R.id.categoryFragment));
        topLevelDestinations.add(R.id.userDetailsFragment);
        topLevelDestinations.add(R.id.favouritesFragment);
        topLevelDestinations.add(R.id.calendarFragment);
        topLevelDestinations.add(R.id.reservationConfirmationFragment);
        topLevelDestinations.add(R.id.commentApprovalFragment);
        topLevelDestinations.add(R.id.pricelistFragment);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (topLevelDestinations.contains(navDestination.getId())) {
                navigationView.setCheckedItem(navDestination.getId());
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                actionBar.setDisplayHomeAsUpEnabled(false);  // Show hamburger icon for top-level
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                actionBar.setDisplayHomeAsUpEnabled(true);  // Show up arrow for other destinations
            }
        });

        mAppBarConfiguration = new AppBarConfiguration
                .Builder(topLevelDestinations)
                .setOpenableLayout(drawer)
                .build();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(intent.getAction()) && data != null) {
            String host = data.getHost();

            if ("accept-invite".equals(host)) {
                String invitationToken = data.getQueryParameter("invitation-token");
                if (invitationToken != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("invitation-token", invitationToken);
                    navController.navigate(R.id.invitationFragment, bundle);
                }
            } else if ("activate-account".equals(host)) {
                String activationToken = data.getQueryParameter("token");
                if (activationToken != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("activation-token", activationToken);
                    navController.navigate(R.id.activateAccountFragment, bundle);
                }
            }
        }

        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        logoutButton.setOnClickListener(v -> {
            logout();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(intent.getAction()) && data != null) {
            String host = data.getHost();

            if ("accept-invite".equals(host)) {
                String invitationToken = data.getQueryParameter("invitation-token");
                if (invitationToken != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("invitation-token", invitationToken);
                    navController.navigate(R.id.invitationFragment, bundle);
                }
            } else if ("activate-account".equals(host)) {
                String activationToken = data.getQueryParameter("activation-token");
                if (activationToken != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("activation-token", activationToken);
                    navController.navigate(R.id.activateAccountFragment, bundle);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void logout(){
        TokenManager tokenManager=new TokenManager(this);
        tokenManager.clearToken();
        MenuInflater inflater = getMenuInflater();
        Menu newMenu = navigationView.getMenu();
        newMenu.clear();
        inflater.inflate(R.menu.unauthenticated_user_nav_menu, newMenu);
        drawer.close();
        navController.navigate(R.id.homeScreenFragment);
        logoutButton.setVisibility(GONE);
        NotificationWebSocketManager.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationWebSocketManager.disconnect();
    }
}