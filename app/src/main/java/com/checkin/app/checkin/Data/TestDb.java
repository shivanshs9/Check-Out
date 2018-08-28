package com.checkin.app.checkin.Data;

import android.content.Context;
import android.util.Log;

import com.checkin.app.checkin.Menu.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.MenuGroupModel;
import com.checkin.app.checkin.Menu.MenuItemModel;
import com.checkin.app.checkin.Menu.MenuModel;
import com.checkin.app.checkin.User.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.Box;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(final Context context) {
        Log.e("TestData", "Populating...");
        AppDatabase.getMenuModel(context).put(new MenuModel(1, "Menu"));
        populateUsers(AppDatabase.getUserModel(context));
        populateMenu(
                AppDatabase.getMenuItemModel(context),
                AppDatabase.getMenuGroupModel(context),
                AppDatabase.getItemCustomizationFieldModel(context),
                AppDatabase.getItemCustomizationGroupModel(context)
        );
    }

    private static void populateMenu(
            Box<MenuItemModel> menuItemModel,
            Box<MenuGroupModel> menuGroupModel,
            Box<ItemCustomizationFieldModel> itemCustomizationFieldModel,
            Box<ItemCustomizationGroupModel> itemCustomizationGroupModel
    ) {
        List<MenuGroupModel> menuGroups = new ArrayList<>(30);
        List<MenuItemModel> menuItems = new ArrayList<>();
        List<ItemCustomizationGroupModel> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationFieldModel> itemCustomizationFields = new ArrayList<>();

        int c1 = 1, c2 = 1;
        for (int i = 1; i <= 30; i++) {
            ArrayList<String> subGroups = null;
            if (i % 2 == 0) {
                subGroups = new ArrayList<>(2);
                subGroups.add("Veg");
                subGroups.add("Non-Veg");
            }
            MenuGroupModel menuGroup = new MenuGroupModel( "Group #" + i, subGroups);
            menuGroup.setMenuId(1);
            menuGroups.add(menuGroup);
            for (int j = 1; j <= i/2+1; j++) {
                List<String> typeName = new ArrayList<>();
                List<Double> typeCost = new ArrayList<>();
                if (j % 3 == 0) {
                    typeName.add("Pint");
                    typeCost.add(300.0);
                    typeName.add("Can");
                    typeCost.add(400.0);
                    typeName.add("Beer");
                    typeCost.add(750.0);
                } else if (j % 3 == 1) {
                    typeName.add("Pint");
                    typeCost.add(300.0);
                    typeName.add("Can");
                    typeCost.add(400.0);
                }
                else {
                    typeName.add("Default");
                    typeCost.add(460.0);
                }
                if (j % 2 == 0) {
                    for (int k = 1; k < 6; k++) {
                        ItemCustomizationGroupModel itemCustomizationGroup = new ItemCustomizationGroupModel((k % 2) * 3, k + 2, "Extra #" + k, c1);
                        c2++;
                        itemCustomizationGroups.add(itemCustomizationGroup);
                        for (int l = 1; l <= 5; l++) {
                            ItemCustomizationFieldModel itemCustomizationField = new ItemCustomizationFieldModel((l & 1) == 0, "Extra name #" + l, l * 20, c2);
                            itemCustomizationFields.add(itemCustomizationField);
                        }
                    }
                }
                MenuItemModel menuItem = new MenuItemModel("Carlsburg #" + j, typeName, typeCost, i, 1);
                menuItem.setDescription("\"It is a 5% abv pilsner beer with a global distribution to 140 mark" +
                        "ets. It was first brewed in 1904, and was created by Carl Jacobsen, son of Carlsberg's founder JC Jacobsen.");
                menuItem.setAvailableMeals(Arrays.asList("brkfst", "lunch", "dinner"));
                menuItem.setIngredients(Arrays.asList("onion", "bread"));
                c1++;

                if (i % 2 == 0)
                    menuItem.setSubGroupIndex(j % 2);
                menuItems.add(menuItem);
            }
        }
        menuGroupModel.put(menuGroups);
        menuItemModel.put(menuItems);
        itemCustomizationGroupModel.put(itemCustomizationGroups);
        itemCustomizationFieldModel.put(itemCustomizationFields);
    }

    private static void populateUsers(Box<UserModel> userModel) {
        UserModel user1 = new UserModel("Alex", "https://78.media.tumblr.com/fc66ed1ceef891684aae8eb2acb152a3/tumblr_oyg8f1Qc1n1vy2tgqo1_400.jpg");
        UserModel user2 = new UserModel("Alice", "https://vignette.wikia.nocookie.net/typemoon/images/c/c1/Archer_EMIYA.jpeg/revision/latest?cb=20150630040614");

        userModel.put(user1, user2);

        UserModel user3 = new UserModel("Monica", "https://vignette.wikia.nocookie.net/typemoon/images/c/c1/Archer_EMIYA.jpeg/revision/latest?cb=20150630040614");
        UserModel user4 = new UserModel("Jack", "https://78.media.tumblr.com/fc66ed1ceef891684aae8eb2acb152a3/tumblr_oyg8f1Qc1n1vy2tgqo1_400.jpg");
        userModel.put(user3, user4);
    }
}
