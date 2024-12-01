package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.catignascabela.dodapplication.databinding.ActivityHomepageBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomepageBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in and load the appropriate fragment
        if (savedInstanceState == null) {
            boolean isTeacher = getIntent().getBooleanExtra("isTeacher", false);
            boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

            // Directly load AdminFragment if isAdmin is true
            if (isAdmin) {
                loadInitialFragment(false, true); // Load AdminFragment directly
            } else {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    loadInitialFragment(isTeacher, false); // Load based on teacher flag
                } else {
                    startLoginActivity(); // Redirect to login if not authenticated
                }
            }
        }
    }

    private void loadInitialFragment(boolean isTeacher, boolean isAdmin) {
        Fragment initialFragment;

        if (isAdmin) {
            initialFragment = new AdminFragment();  // Load AdminFragment if the user is an admin
        } else if (isTeacher) {
            initialFragment = new TeacherHomeFragment();  // Load TeacherHomeFragment if the user is a teacher
        } else {
            initialFragment = new StudentHomeFragment();  // Load StudentHomeFragment if the user is a student
        }

        // Add the fragment to the fragment container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commit();
        binding.navigationView.setCheckedItem(R.id.nav_home); // Highlight the home item
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            boolean isTeacher = getIntent().getBooleanExtra("isTeacher", false);
            boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

            if (isAdmin) {
                loadInitialFragment(false, true); // Load AdminFragment directly
            } else if (currentUser != null) {
                loadInitialFragment(isTeacher, false);
            } else {
                startLoginActivity(); // Redirect to login if not authenticated
            }
        } else if (id == R.id.nav_violations) {
            selectedFragment = new ViolationsFragment();
        } else if (id == R.id.nav_logout) {
            showLogoutConfirmationDialog(); // Show confirmation dialog for logout
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())   // Call the logout method on confirmation
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())  // Dismiss dialog on cancellation
                .show();  // Show dialog
    }

    private void logout() {
        firebaseAuth.signOut();

        startLoginActivity(); // Redirect to login activity after logout
    }

    private void startLoginActivity() {
        Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent); // Start LoginActivity

        finish(); // Close this activity so it cannot be returned to
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START); // Close drawer if open
        } else {
            super.onBackPressed(); // Otherwise, handle back press normally
        }
    }
}
