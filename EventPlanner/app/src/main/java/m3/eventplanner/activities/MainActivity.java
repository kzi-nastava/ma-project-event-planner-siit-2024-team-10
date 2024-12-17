package m3.eventplanner.activities;

import static android.view.View.GONE;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.fragments.LoginFragment;

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
        topLevelDestinations.add(R.id.chatFragment);
        topLevelDestinations.add(R.id.notificationFragment);

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
                .Builder(R.id.loginFragment,R.id.homeScreenFragment,R.id.manageOfferingsFragment, R.id.chatFragment, R.id.notificationFragment)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        logoutButton.setOnClickListener(v -> {
            logout();
        });
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

    private void logout(){
        TokenManager tokenManager=new TokenManager(this);
        tokenManager.clearToken();
        MenuInflater inflater = getMenuInflater();
        Menu newMenu = navigationView.getMenu();
        newMenu.clear();
        inflater.inflate(R.menu.unauthenticated_user_nav_menu, newMenu);
        drawer.close();
        navController.navigate(R.id.homeScreenFragment);
        logoutButton.setVisibility(GONE);
    }
}