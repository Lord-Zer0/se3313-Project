package com.example.matthew.contextswitchandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String [] itemList;
    String selectedItem;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemList = new String[]{"Kettle",
                "Toaster",
                "Spatula",
                "Spoon",
                "Cup",
                "Fork",
                "Mug",
                "Spoon",
                "Bowl",
                "Plate",
                "Placemat",
                "Mixing bowl",
                "Glass",
                "Fruit bowl",
                "Clock",
                "Curtain",
                "Cushion",
                "Picture",
                "Stereo",
                "DVD",
                "CD",
                "Vase",
                "Book",
                "Jug",
                "Saucepan",
                "Knife",
                "Hook",
                "Clothes",
                "Shoe",
                "Soap",
                "Washing powder",
                "Sponge",
                "Cloth",
                "Baking tray",
                "Cake tin",
                "Rug",
                "Towel",
                "Quilt",
                "Mirror",
                "Broom",
                "Brush",
                "Garbage",
                "Paper",
                "Pen",
                "Pencil",
                "Light",
                "Vacuum",
                "Shelves",
                "Toilet roll",
                "Food",
                "Drink",
                "Duster",
                "Coaster",
                "Plant",
                "Bucket",
                "TV",
                "DVD player",
                "Box",
                "Clothes hanger",
                "Tin",
                "Jewellery",
                "Toiletries",
                "Sheet",
                "Pillow",
                "Pillow case",
                "Computer",
                "Laptop",
                "Kitchen scale",
                "Bathroom scale",
                "Lamp",
                "Doormat",
                "Blinds"
        };
        Random rand = new Random();
        selectedItem = itemList[rand.nextInt(itemList.length)];
        textView = (TextView) findViewById(R.id.textView);
        //textView.setText(itemList[rand.nextInt(itemList.length)]);
        textView.setText(selectedItem);
        final Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        button.setOnClickListener(onClickListener);
        button2.setOnClickListener(onClickListener);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    Random rand = new Random();
                    selectedItem = itemList[rand.nextInt(itemList.length)];
                    textView = (TextView) findViewById(R.id.textView);
                    textView.setText(selectedItem);
                    break;
                case R.id.button2:
                    System.out.println(selectedItem);
                    break;

            }

        }
    };
}

