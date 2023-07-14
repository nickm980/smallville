package io.github.nickm980.smallville.entities;

import java.util.List;

public class NaturalLanguageMapper {

    public static String fromDialog(Dialog dialog) {
	return dialog.getName() + " said " + dialog.getMessage();
    }
    
    public static String fromLocation(String key, LocationManager location) {
	String children = String.join(",", location.getChildren(key));
	
	
	return children;
    }
}
