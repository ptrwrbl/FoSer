package pollub.pam.foser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickStart(View view) {
        Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show();
    }

    public void clickStop(View view) {
        Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();
    }

    public void clickRestart(View view) {
        clickStop(view);
        clickStart(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_settings: startActivity(new Intent(this,SettingsActivity.class)); return true;
            case R.id.item_exit: finishAndRemoveTask(); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}