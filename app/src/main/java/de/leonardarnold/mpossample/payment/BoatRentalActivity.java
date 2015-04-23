package de.leonardarnold.mpossample.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.Constants;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.injection.BaseActivity;

/**
 * This activity was made to get a fast and little impression about
 * what you can do in your own app. This screen was not made very
 * dynamically. If you want to implement something similar you may
 * should rethink his architecture.
 */
public class BoatRentalActivity extends BaseActivity {

    @InjectView(R.id.hour_spinner_adria)
    Spinner adriaSpinner;
    @InjectView(R.id.hour_spinner_motorboat)
    Spinner motorboatSpinner;
    @InjectView(R.id.hour_spinner_sailing_boat)
    Spinner sailingboatSpinner;
    @InjectView(R.id.amount_textview)
    TextView amountTextView;

    private int amount;
    private double adriaAmount;
    private double motorboatAmount;
    private double sailingboatAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boatrental);
        ButterKnife.inject(this);

        //--- Spinner Adria
        final ArrayAdapter<CharSequence> adapterAdria = ArrayAdapter.createFromResource(this,
                R.array.hour_array, android.R.layout.simple_spinner_item);
        adapterAdria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adriaSpinner.setAdapter(adapterAdria);
        adriaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calculateAmount(Boat.ADRIA, adapterAdria.getItem(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //--- Spinner Motorboat
        final ArrayAdapter<CharSequence> adapterMotorboat = ArrayAdapter.createFromResource(this,
                R.array.hour_array, android.R.layout.simple_spinner_item);
        adapterMotorboat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        motorboatSpinner.setAdapter(adapterMotorboat);
        motorboatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calculateAmount(Boat.MOTORBOAT, adapterMotorboat.getItem(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //--- Spinner Motorboat
        final ArrayAdapter<CharSequence> adapterSailingboat = ArrayAdapter.createFromResource(this,
                R.array.hour_array, android.R.layout.simple_spinner_item);
        adapterSailingboat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sailingboatSpinner.setAdapter(adapterMotorboat);
        sailingboatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calculateAmount(Boat.SAILINGBOAT, adapterSailingboat.getItem(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    /**
     * calculates amount per boat
     */
    private void calculateAmount(Boat boat, String hours) {
        switch (boat) {
            case ADRIA:
                adriaAmount = Double.parseDouble(hours) * 25;
                break;
            case MOTORBOAT:
                motorboatAmount = Double.parseDouble(hours) * 35;
                break;
            case SAILINGBOAT:
                sailingboatAmount = Double.parseDouble(hours) * 40;
                break;
            default:
        }
        double temp = adriaAmount + motorboatAmount + sailingboatAmount;
        this.amount = (int)(temp*100);
        amountTextView.setText(temp + "0€");

    }

    /**
     * finish activity and give amount back as result
     */
    public void continuePayment(View view){
        Intent intent = new Intent();
        intent.putExtra(Constants.BOATRENTAL_AMOUNT, this.amount);
        setResult(RESULT_OK, intent);
        this.finish();
    }


    public enum Boat {
        ADRIA, MOTORBOAT, SAILINGBOAT;
    }
}
