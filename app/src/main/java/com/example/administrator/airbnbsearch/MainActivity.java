package com.example.administrator.airbnbsearch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.administrator.airbnbsearch.domain.Reservation;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
    private TextView guestTxt;
    private Button btnGuestMinus;
    private Button btnGuestPlus;

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
        guestTxt = (TextView) findViewById(R.id.guestTxt);
        btnGuestMinus = (Button) findViewById(R.id.btnGuestMinus);
        btnGuestPlus = (Button) findViewById(R.id.btnGuestPlus);
    }

    private void setListener() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
//                String selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;
            // 완성도를 높이기 위해 포맷을 사용했다, 두자리 수 일 때 0을 채워준다
            selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            switch (checkStatus) {
                case CHECK_IN:
                    if (reservation.checkOut != null) {
                        if (dayOfMonth > Integer.parseInt(reservation.checkOut.split("-")[2])) {
                            reverseInOut();
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
                            reverseOutIn();
                            // break 해 주지 않으면 밑에 코드가 계속 실행되기 때문에 의미가 없음
                            break;
                        }
                    }
            }
        });

        fab.setOnClickListener(view -> search());

        btnCheckIn.setOnClickListener(v -> {
            checkStatus = CHECK_IN;
            setButtonText(btnCheckIn, getString(R.string.hint_start_date), "Select Date");
            setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
        });

        btnCheckOut.setOnClickListener(v -> {
            checkStatus = CHECK_OUT;
            setButtonText(btnCheckOut, getString(R.string.hint_end_date), "Select Date");
            setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
        });

        btnGuestMinus.setOnClickListener(v->{
            reservation.setGuestMinus();
            guestTxt.setText(reservation.guest+"");
        });
        btnGuestPlus.setOnClickListener(v->{
            reservation.setGuestPlus();
            guestTxt.setText(reservation.guest+"");
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

    private void reverseInOut() {
        final String checkOutTemp = reservation.checkOut;
        reservation.checkIn = checkOutTemp;
        reservation.checkOut = selectedDate;
        setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
        setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
    }

    private void reverseOutIn() {
        // final 을 이렇게 이용할 수 있다
        final String checkInTemp = reservation.checkIn;
        reservation.checkOut = checkInTemp;
        reservation.checkIn = selectedDate;
        setButtonText(btnCheckIn, getString(R.string.hint_start_date), reservation.checkIn);
        setButtonText(btnCheckOut, getString(R.string.hint_end_date), reservation.checkOut);
    }

    private void search() {
        // 1. 레트로핏 생성
        Retrofit client = new Retrofit.Builder()
                .baseUrl(ISearch.SERVER)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // 2. 서비스 연결
        ISearch mySearch = client.create(ISearch.class);

        // 3. 서비스의 특정 함수 호출
        Observable<ResponseBody> observable = mySearch
                .get(reservation.checkIn, reservation.checkOut, reservation.guest, -1, -1, -1, -1);

        // 4. subscribe 등록
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            //  데이터를 꺼내고
                            String jsonString = responseBody.string();
                            Log.e("Retrofit", jsonString);
                        }
                );
    }

}
