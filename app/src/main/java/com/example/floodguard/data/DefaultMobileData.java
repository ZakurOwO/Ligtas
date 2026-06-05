package com.example.floodguard.data;

import com.example.floodguard.model.Alert;
import com.example.floodguard.model.EmergencyContactModel;
import com.example.floodguard.model.GoBagItemModel;
import com.example.floodguard.model.SafetyTipModel;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public final class DefaultMobileData {
    private DefaultMobileData() {}

    public static List<EmergencyContactModel> emergencyContacts() {
        List<EmergencyContactModel> contacts = new ArrayList<>();
        contacts.add(contact("mdrrmo", "MDRRMO", "911", "mdrrmo", "blue", 1));
        contacts.add(contact("barangay_hall", "Barangay Hall", "(02) 8888-1234", "barangay_hall", "green", 2));
        contacts.add(contact("pnp", "Philippine National Police", "117", "pnp", "purple", 3));
        contacts.add(contact("bfp", "Bureau of Fire Protection", "160", "bfp", "orange", 4));
        contacts.add(contact("ndrrmc", "NDRRMC Hotline", "8911-5061", "other", "red", 5));
        return contacts;
    }

    public static List<Alert> alerts() {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(alert(
                "Typhoon Carina Heavy Rainfall Warning",
                "Maging maingat sa baha. Lagpas dibdib na baha",
                "Critical",
                "All Barangays",
                "Active",
                2026,
                Calendar.JUNE,
                5,
                10,
                9
        ));
        alerts.add(alert(
                "test",
                "hello world",
                "Warning",
                "All Barangays",
                "Active",
                2026,
                Calendar.JUNE,
                5,
                2,
                43
        ));
        return alerts;
    }

    public static List<SafetyTipModel> safetyTips() {
        List<SafetyTipModel> tips = new ArrayList<>();
        tips.add(tip("before_1", "before_flood", "Prepare your go bag", "Pack water, food, flashlight, power bank, radio, medicine, IDs, cash, and important documents in a waterproof pouch.", 1));
        tips.add(tip("before_2", "before_flood", "Monitor official warnings", "Follow MDRRMO, barangay, PAGASA, and NDRRMC advisories. Do not rely on rumors during heavy rain.", 2));
        tips.add(tip("before_3", "before_flood", "Plan your evacuation route", "Know the nearest evacuation center and agree on a family meeting point before floodwater rises.", 3));

        tips.add(tip("during_1", "during_flood", "Move to higher ground", "Leave low-lying areas early. Avoid waiting until roads are already flooded.", 4));
        tips.add(tip("during_2", "during_flood", "Avoid walking through floodwater", "Floodwater may hide open drains, sharp objects, strong current, or electrical hazards.", 5));
        tips.add(tip("during_3", "during_flood", "Turn off electricity if safe", "Switch off the main breaker before water reaches outlets or appliances.", 6));

        tips.add(tip("after_1", "after_flood", "Return only when cleared", "Wait for officials to confirm that your area is safe before going home.", 7));
        tips.add(tip("after_2", "after_flood", "Clean and disinfect", "Wear boots and gloves when cleaning. Disinfect floors, walls, and items touched by floodwater.", 8));
        tips.add(tip("after_3", "after_flood", "Report damage and hazards", "Report damaged power lines, blocked roads, landslides, or trapped residents to local responders.", 9));

        tips.add(tip("evacuation_1", "evacuation", "Evacuate early", "Bring your go bag, medicines, IDs, phone, charger, and drinking water. Lock your home if time allows.", 10));
        tips.add(tip("evacuation_2", "evacuation", "Help children and seniors first", "Keep children, older adults, pregnant persons, and people with disabilities close to a helper.", 11));
        tips.add(tip("evacuation_3", "evacuation", "Follow marked routes", "Use official evacuation routes and avoid shortcuts through rivers, canals, or flooded roads.", 12));

        tips.add(tip("water_1", "water_safety", "Do not drink floodwater", "Use sealed bottled water or boiled water. Floodwater can contain sewage, chemicals, and disease.", 13));
        tips.add(tip("water_2", "water_safety", "Protect open wounds", "Cover cuts with waterproof bandages and wash with clean water and soap after exposure.", 14));
        tips.add(tip("water_3", "water_safety", "Throw away unsafe food", "Discard food touched by floodwater, including food in damaged packaging.", 15));

        tips.add(tip("first_aid_1", "first_aid", "Treat minor wounds", "Wash with clean water and soap, apply antiseptic, and cover with a sterile bandage.", 16));
        tips.add(tip("first_aid_2", "first_aid", "Watch for illness", "Seek medical help for fever, vomiting, diarrhea, infected wounds, or animal bites after flooding.", 17));
        tips.add(tip("first_aid_3", "first_aid", "Call emergency services", "Call responders immediately for chest pain, trouble breathing, severe bleeding, or unconsciousness.", 18));
        return tips;
    }

    public static List<GoBagItemModel> goBagItems() {
        List<GoBagItemModel> items = new ArrayList<>();
        items.add(item("water", "Drinking water for each person", "Food and Water", true, 1));
        items.add(item("food", "Ready-to-eat food for 24 hours", "Food and Water", true, 2));
        items.add(item("first_aid", "First aid kit and maintenance medicines", "Health", true, 3));
        items.add(item("documents", "IDs and important documents in a waterproof pouch", "Documents", true, 4));
        items.add(item("phone_power", "Fully charged phone and power bank", "Communication", true, 5));
        items.add(item("flashlight", "Flashlight with extra batteries", "Tools", true, 6));
        items.add(item("radio", "Battery-powered radio for official advisories", "Communication", false, 7));
        items.add(item("clothes", "Dry clothes, raincoat, and sturdy footwear", "Personal", false, 8));
        items.add(item("hygiene", "Hygiene kit, masks, and alcohol", "Personal", false, 9));
        items.add(item("cash", "Emergency cash and small bills", "Documents", false, 10));
        items.add(item("electricity", "Turn off electricity if water is entering the house", "During Flood", true, 11));
        items.add(item("higher_ground", "Move children, seniors, pets, and valuables to higher ground", "During Flood", true, 12));
        items.add(item("avoid_water", "Do not walk or drive through floodwater", "During Flood", true, 13));
        items.add(item("evac_route", "Confirm the nearest evacuation route and center", "Evacuation", true, 14));
        items.add(item("contacts", "Save emergency contacts and barangay hotlines", "Communication", true, 15));
        items.add(item("whistle", "Whistle or signal light for rescue attention", "Tools", false, 16));
        return items;
    }

    private static Alert alert(String title, String message, String type, String location, String status, int year, int month, int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Manila"));
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Alert alert = new Alert();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setType(type);
        alert.setLocation(location);
        alert.setStatus(status);
        alert.setCreatedAt(new Timestamp(calendar.getTime()));
        alert.setUpdatedAt(new Timestamp(calendar.getTime()));
        return alert;
    }

    private static EmergencyContactModel contact(String id, String name, String number, String type, String color, int order) {
        EmergencyContactModel contact = new EmergencyContactModel();
        contact.setContactId(id);
        contact.setName(name);
        contact.setNumber(number);
        contact.setType(type);
        contact.setIconColor(color);
        contact.setOrder(order);
        contact.setActive(true);
        return contact;
    }

    private static SafetyTipModel tip(String id, String category, String title, String content, int order) {
        SafetyTipModel tip = new SafetyTipModel();
        tip.setTipId(id);
        tip.setCategory(category);
        tip.setTitle(title);
        tip.setContent(content);
        tip.setOrder(order);
        tip.setActive(true);
        return tip;
    }

    private static GoBagItemModel item(String id, String name, String category, boolean essential, int order) {
        GoBagItemModel item = new GoBagItemModel();
        item.setItemId(id);
        item.setName(name);
        item.setCategory(category);
        item.setEssential(essential);
        item.setOrder(order);
        return item;
    }
}
