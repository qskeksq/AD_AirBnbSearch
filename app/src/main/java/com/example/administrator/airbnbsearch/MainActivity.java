package com.example.administrator.airbnbsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.administrator.airbnbsearch.domain.Reservation;

public class MainActivity extends AppCompatActivity {

    Button btnCheckIn, btnCheckOut;
    Toolbar toolbar;
    FloatingActionButton fab;
    CalendarView calendarView;

    // 항상 이렇게 데이터를 저장하기 위한 클래스를 만들어 줘야 한다. 뷰에서 클릭하고, 받아온 수는 모두 객체로 만들어서 저장해 둬야 한다.
    Reservation reservation;

    private static final int CHECK_IN = 10;
    private static final int CHECK_OUT = 11;
    private int checkStatus = CHECK_IN;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setCalendarButtonText();
        setListener();
        init();
    }

    private void init() {
        reservation = new Reservation();
    }

    private void initView() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
        btnCheckOut = (Button) findViewById(R.id.btnCheckOut);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);
    }

    private void setListener() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                String selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;
                // 완성도를 높이기 위해 포맷을 사용했다, 두자리 수 일 때 0을 채워준다
                selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                switch (checkStatus) {
                    case CHECK_IN:
                        if (reservation.checkOut != null) {
                            if (dayOfMonth > Integer.parseInt(reservation.checkOut.split("-")[2])) {
                                reverseInOut(dayOfMonth);
                                // break 해 주지 않으면 밑에 코드가 계속 실행되기 때문에 의미가 없음
                                break;
                            }
                        }
                        reservation.checkIn = selectedDate;
                        setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
                        break;
                    case CHECK_OUT:
                        if (reservation.checkIn != null) {
                            if (dayOfMonth < Integer.parseInt(reservation.checkIn.split("-")[2])) {
                                reverseOutIn(dayOfMonth);
                                // break 해 주지 않으면 밑에 코드가 계속 실행되기 때문에 의미가 없음
                                break;
                            }
                        }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatus = CHECK_IN;
                setButtonText(btnCheckIn, getString(R.string.hint_start_date), "Select Date");
                setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatus = CHECK_OUT;
                setButtonText(btnCheckOut, getString(R.string.hint_end_date), "Select Date");
                setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
            }
        });
    }

    // 이런식으로 패턴화된 코드를 만들어서 점점 코딩을 줄여나가야 한다.
    private void setCalendarButtonText() {
        setButtonText(btnCheckIn, getString(R.string.hint_start_date), getString(R.string.hint_select_date));
        setButtonText(btnCheckOut, getString(R.string.hint_end_date), "-");
    }

    private void setButtonText(Button btn, String upText, String downText) {
        String btnCheckInText = upText + "<br> <font color=#fd5a5f>" + downText + "</font>";
        StringUtil.setHtmlText(btn, btnCheckInText);
    }

    private void reverseInOut(int dayOfMonth) {
        final String checkOutTemp = reservation.checkOut;
        reservation.checkIn = checkOutTemp;
        reservation.checkOut = selectedDate;
        setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
        setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
    }

    private void reverseOutIn(int dayOfMonth) {
                // final 을 이렇게 이용할 수 있다
        final String checkInTemp = reservation.checkIn;
        reservation.checkOut = checkInTemp;
        reservation.checkIn = selectedDate;
        setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
        setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
    }

}
