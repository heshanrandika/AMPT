/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ampt.bluetooth;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DogScanActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    DatabaseHelper daf = new DatabaseHelper(this);
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


   /* byte[] outImage={-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,72,0,0,0,72,8,6,0,0,0,85,-19,-77,71,0,0,0,4,115,66,73,84,8,8,8,8,124,8,100,-120,0,0,23,-125,73,68,65,84,120,-100,-51,-100,121,124,92,-59,-107,-17,-65,117,111,111,-38,55,107,-79,36,-37,-14,-126,55,-39,-118,-15,10,24,99,-127,-127,4,94,18,32,-79,76,24,96,-110,64,-30,-105,33,-120,36,51,124,88,38,76,-30,-40,-55,-125,100,8,24,-104,76,32,30,-78,-16,38,60,-126,32,3,38,4,12,6,-124,109,112,-80,45,47,-78,4,-78,-83,-106,-68,-54,82,75,-74,-92,-42,-38,125,-105,122,127,-12,-19,-42,109,-75,-70,37,111,-112,-33,-25,-45,-97,-18,91,85,-73,-18,-87,95,-99,58,117,-22,84,-35,22,124,-122,-112,82,10,-21,-89,-120,87,4,64,8,33,63,29,-119,98,17,79,-80,11,2,-117,16,-59,122,-82,20,66,24,99,-68,79,13,-33,3,-104,-97,38,97,23,-100,32,41,-91,66,-120,20,83,8,97,-114,-112,-17,6,-46,-128,36,-64,101,37,7,-127,1,-96,71,8,17,56,-45,58,-49,39,46,24,65,-31,94,23,66,-24,-74,-76,28,-96,12,88,4,-52,5,-90,0,5,64,-122,-108,50,9,112,88,69,117,96,64,8,-47,13,-76,2,77,64,45,-80,11,-88,21,66,-100,-78,-43,-23,-32,12,-76,-15,76,113,-34,9,-78,4,54,-62,-61,64,74,-103,15,124,17,-72,1,88,34,-91,-52,-77,-46,-49,-88,94,33,68,-8,-37,7,-4,13,-40,8,-4,69,8,-47,102,-43,39,0,-43,-34,33,-25,3,-25,-115,32,75,99,100,88,-27,-91,-108,-105,2,-33,6,110,-108,82,102,89,105,0,-90,-11,-79,63,95,-116,32,-117,-76,62,-40,-66,-61,-74,8,33,4,66,-120,78,-32,21,96,-125,16,98,-69,-11,12,37,-108,125,126,52,-22,-68,16,36,-91,116,-124,123,78,74,89,14,-4,16,-72,90,74,25,38,-59,32,-44,-56,-80,-19,56,23,-124,9,22,-124,8,11,-109,-75,25,-8,-103,16,-94,122,-72,76,-25,-126,115,34,-56,-22,45,-124,16,-90,-108,114,50,-16,11,96,-91,-115,24,29,91,-81,95,0,72,66,-28,59,44,57,16,66,-68,4,-36,39,-124,104,-74,-53,119,-74,15,56,107,-63,-91,-108,78,33,-124,102,-3,-2,30,-16,-120,-108,-46,35,67,-52,24,92,88,98,98,-60,-79,63,83,81,-108,0,112,-65,16,-30,-119,-31,-78,-98,41,-50,-86,1,-31,7,74,41,51,-128,-1,-106,82,126,-47,-46,24,13,112,-98,77,-99,-25,17,26,-32,-76,-76,-23,117,-32,86,33,68,-9,-39,-110,116,-58,4,-39,-56,-103,3,-4,69,74,57,73,74,-87,17,-22,-67,115,-75,47,-25,11,38,33,-115,114,10,33,-114,104,-102,-10,69,-73,-37,93,119,54,36,-99,17,65,97,-61,39,-91,-68,22,-40,40,-91,116,91,-28,124,-42,90,19,15,97,-39,2,-102,-90,-35,-32,-15,120,54,-99,-87,-15,30,115,-113,91,-20,-21,82,-54,-81,2,-101,76,-45,116,75,41,117,-2,126,-55,-127,-112,108,58,-32,118,56,28,111,-10,-12,-12,84,88,109,24,-77,-52,99,-46,32,-37,-80,-70,6,120,-53,52,77,-84,7,59,18,-33,-7,119,3,93,8,-31,48,12,3,-65,-33,127,93,78,78,-50,-101,99,29,110,-93,18,100,27,86,-91,64,-115,105,-102,110,18,-112,-93,-23,38,-67,-3,65,-6,3,58,-102,110,32,16,120,-36,42,30,-105,-125,-76,100,23,-118,114,97,39,54,127,95,-128,64,-48,32,35,-43,-115,-53,-87,-38,-77,116,33,-124,67,-45,-76,64,123,123,-5,-110,-30,-30,-30,125,99,25,110,9,53,64,74,-87,88,-28,-92,19,50,-56,49,-28,4,53,-125,-35,7,124,-20,58,-40,-114,-9,68,23,39,-38,123,-23,-24,30,96,32,-96,-93,27,18,33,-64,-27,80,73,-10,56,-56,-53,76,-94,40,47,-115,-78,-87,-29,-72,100,118,1,-109,-57,-89,-97,55,-62,78,118,-12,-79,-31,-75,58,62,-84,107,-95,63,-96,83,-100,-105,-58,-11,-105,-108,-16,-107,43,-90,-110,-20,113,2,56,-92,-108,-70,-45,-23,116,103,103,103,-65,-70,103,-49,-98,121,66,-120,46,-85,-115,113,-3,-92,-124,-46,-123,111,54,77,-13,53,-32,-117,-90,105,-22,66,-120,8,57,-37,-21,79,-14,-101,-115,117,124,-12,113,43,65,109,-20,-98,-67,41,33,43,-35,-61,-118,-7,-59,-84,-2,-46,28,-90,21,103,-114,-7,-34,-111,-48,-39,51,-56,-35,-113,87,-13,-47,-57,-83,-47,25,66,-16,-113,-41,-50,-28,-2,-37,22,-30,-74,-76,73,74,-87,11,33,28,-35,-35,-35,111,100,103,103,-1,47,41,-91,56,43,-126,-62,-22,103,24,70,-91,16,-30,73,-5,108,-43,114,-86,-113,-1,-69,-87,-127,23,-33,61,72,119,111,76,52,2,-128,-62,113,-87,-76,-100,-22,-125,4,-117,82,9,76,-52,79,103,-11,-105,-25,112,-45,-78,-87,120,92,106,-36,-78,-119,-16,-89,-9,26,-7,-31,51,-37,70,-52,83,85,-123,-107,-27,23,-15,47,55,95,76,118,-70,7,0,33,-124,-90,105,-102,-77,-67,-67,-3,-34,-30,-30,-30,95,38,26,106,35,-50,98,82,74,-43,26,90,-109,20,69,121,-60,114,2,85,-128,99,-66,30,-18,126,-30,125,-98,121,117,127,92,114,0,110,-66,106,58,95,-69,106,58,-120,-8,74,42,-128,99,109,126,126,-12,-20,118,126,-9,122,-3,25,-81,-16,-61,104,-23,-24,-115,-101,103,24,38,47,-68,115,-128,71,95,-40,77,80,55,34,-19,115,-71,92,100,102,102,-82,-83,-85,-85,-101,102,-75,117,-60,-34,73,56,-51,27,-122,-15,11,41,101,-78,53,-99,43,-122,41,121,-26,-43,58,-10,30,-16,49,-102,-23,-16,-72,29,60,120,-37,66,110,-71,106,58,-26,40,-19,-106,-90,-28,-65,94,-81,-89,122,-49,-119,-60,5,-29,32,-55,-99,120,50,21,-64,43,91,-67,-68,-69,-5,120,56,73,49,12,67,-9,120,60,-55,57,57,57,63,39,-63,72,-118,33,-56,82,55,99,96,96,-32,10,85,85,87,-55,-48,32,117,96,61,-28,-49,91,27,71,37,39,-116,-108,36,39,-1,-6,-113,-117,-8,-62,-30,73,9,53,9,-96,-69,55,-64,79,-97,-37,65,-83,-73,99,108,-107,-37,48,119,114,-50,-16,25,43,6,65,-51,-32,-9,111,124,66,119,95,16,0,33,-124,67,85,85,-103,-111,-111,-15,-107,-3,-5,-9,95,35,-124,48,-84,88,86,20,70,-46,32,19,-64,-23,116,-2,-48,22,-86,-32,72,91,15,-113,87,-19,37,24,60,-77,48,75,-110,-37,-63,47,-2,105,41,-73,92,53,-99,-47,-68,-118,35,-83,126,-2,-29,-27,90,6,-49,-16,25,11,103,-28,113,-39,-100,-62,81,-53,-19,-8,-92,-107,-86,-22,-58,-56,-75,105,-102,-122,-37,-19,38,55,55,-9,126,75,-72,24,99,29,69,-112,101,123,-52,-2,-2,-2,-59,-86,-86,94,107,-73,61,-101,62,58,76,75,123,-4,-79,-98,8,-87,-55,46,30,-68,125,33,-73,94,51,-125,-47,-84,-52,-42,-38,19,108,-83,109,-119,-101,31,8,-22,-12,15,70,-5,119,110,-105,-54,-67,-73,-52,103,114,97,70,-62,-70,21,-32,-7,-51,13,-100,-24,-24,11,39,-87,-118,-94,-112,-102,-102,122,85,77,77,-51,-27,86,-40,70,29,126,-113,29,2,64,81,-108,-43,-74,120,-114,-48,116,-109,119,118,31,31,-13,-48,26,9,-55,30,39,-9,-35,-70,-128,91,86,36,54,-36,-102,110,-80,101,-49,-15,-104,-12,-34,-127,32,-49,108,-84,-29,-26,-75,-101,-72,-23,71,111,-16,-64,-45,31,-48,-39,51,52,73,-52,-100,-104,-59,-41,-81,-101,-51,104,90,-38,-36,-30,103,-21,-66,-120,-83,19,-90,105,-22,30,-113,-121,-100,-100,-100,-43,-31,52,123,-7,8,65,97,-89,-48,-17,-9,-113,115,-71,92,55,88,4,41,0,7,-114,-98,-90,-31,-56,-23,-124,15,30,11,82,60,78,30,-72,117,33,-41,44,-100,-104,-48,112,-17,-13,-74,71,105,-119,97,74,-42,87,-19,-27,-47,-1,87,67,93,99,59,-34,-93,-89,-87,-86,62,-60,-125,-65,-7,-112,-45,-2,-63,72,-71,-21,-105,76,98,-10,-108,-100,-124,50,-88,2,106,26,-38,-20,73,-118,-86,-86,100,100,100,92,-9,-30,-117,47,22,89,51,90,-124,23,-69,6,-123,127,127,25,24,71,-56,-10,40,0,-17,-20,62,78,-17,64,-20,-78,-59,-108,-32,78,114,-115,58,75,-11,-10,7,-47,44,71,50,53,-39,-59,99,119,47,-29,-74,107,102,-60,-43,-92,-3,-51,-89,-39,90,55,-28,-12,-67,-16,-50,65,-98,125,-3,-29,40,55,64,0,111,-17,60,-62,-9,-98,120,-97,-98,-2,-112,-31,-51,78,-9,-16,-48,109,11,73,77,118,13,-81,50,10,91,-21,78,-38,-51,-123,34,-91,52,60,30,79,-50,69,23,93,116,99,56,-115,-31,63,8,-7,109,-62,-27,114,125,25,-94,119,29,-22,-101,79,-113,-88,-72,101,-45,-58,-79,-31,-34,-85,-104,51,74,-81,-67,95,-37,-62,-81,55,-42,97,24,33,27,-104,-28,118,112,-1,-83,11,-7,-121,56,-61,77,1,26,-113,117,70,-82,15,28,57,-115,58,-126,0,2,-8,-37,-57,-83,-68,-75,-13,104,36,109,-63,-52,124,-54,47,46,78,40,-49,-87,-82,1,118,31,-12,69,-82,77,-45,-60,-14,-117,-82,103,104,-125,50,34,11,-106,-69,109,-44,-42,-42,102,58,-99,-50,-59,82,74,-124,-75,-49,-30,-21,-20,-25,-64,-15,78,-122,99,-58,-92,108,-42,-35,121,41,-105,-107,22,-16,127,-66,125,25,51,38,101,-57,-43,-92,-127,-96,-63,-29,47,-19,99,-61,107,117,-24,22,73,41,73,78,30,-68,125,17,-73,95,59,51,-122,36,33,66,-92,-104,86,-123,-119,-42,107,82,74,-86,-34,59,20,-47,34,85,17,-36,-78,98,58,-55,73,-15,35,26,82,74,118,54,12,17,36,-124,16,-106,-79,-98,-65,126,-3,-6,60,107,-54,23,17,-126,-62,-33,-71,-71,-71,-13,-124,16,-29,25,-38,-127,-32,72,-85,-97,-45,93,3,81,15,48,36,44,45,29,-49,-20,-110,108,0,-26,76,-55,-31,-47,-17,46,99,-50,-108,-100,-72,-77,-108,-90,27,60,-15,-14,62,94,-39,-30,-115,-92,37,-71,29,-36,-5,-75,-7,44,-103,-107,31,83,-66,-71,-75,-121,-63,96,-56,-5,-97,92,-104,-127,-111,96,24,-17,62,-24,-29,-19,93,-57,34,-41,-117,102,-27,83,54,101,92,-4,27,-128,-35,-34,14,6,2,-111,-43,-123,2,-56,-92,-92,-92,-126,-117,47,-66,120,1,64,85,85,-107,18,-50,0,-53,114,-69,-35,-18,-123,-42,117,-60,17,57,-22,-21,-91,63,16,-67,76,81,5,-4,113,-13,1,-2,-16,-58,39,-111,-95,56,107,98,22,-21,-17,89,-50,-30,-39,5,35,-110,100,18,26,-110,-13,103,-28,69,-91,-65,-78,-83,-119,-38,-90,83,49,-27,-113,-74,-7,105,61,-35,15,-64,-14,-117,-117,41,-50,77,-115,-37,88,-45,-108,108,-36,-30,-115,52,88,17,-126,-59,-77,-14,19,-70,20,39,125,61,28,107,-13,71,-82,-91,-108,-122,-45,-23,36,35,35,99,1,32,42,42,42,-94,52,72,2,-118,-53,-27,-102,107,109,-39,68,116,-38,-41,53,48,-30,122,51,16,-44,-7,-27,-117,-69,121,-31,-35,-58,-120,32,83,-58,-89,-13,-77,111,95,-58,-20,-110,97,-102,36,-95,-76,36,-101,71,86,95,-58,20,-101,-81,-14,-25,45,94,126,-2,124,13,3,-125,-79,19,64,103,111,-112,-45,-67,-95,97,83,-110,-97,-58,-75,-117,38,38,104,46,124,-44,-48,-58,39,-121,-121,102,-38,89,37,57,104,9,102,-113,-18,-66,32,-34,-106,33,-126,76,-45,20,-86,-86,-110,-110,-110,82,-118,-51,14,41,22,25,38,32,84,85,-99,60,-68,34,127,111,48,-82,-1,19,8,-24,60,-4,-57,-99,-68,-65,119,-56,111,-103,50,62,-99,-121,-1,-9,82,-90,21,-90,15,9,59,33,-109,-11,119,93,-50,-28,-15,67,-28,-68,-70,-51,-53,-70,-25,118,-48,63,16,28,-79,110,9,-8,-5,-121,-14,22,-51,42,-120,117,115,109,8,-22,6,-11,54,87,100,82,65,26,-23,9,102,51,-61,-108,-76,15,51,29,-118,-94,-32,116,58,39,17,114,-114,77,41,-91,8,111,-72,-55,-21,-82,-69,-50,-95,-86,106,-66,117,29,-71,-55,-33,23,127,-59,14,-48,-33,31,-28,-89,127,-40,65,-110,-53,-63,-110,-39,5,0,-52,-103,-100,-115,105,102,69,-54,-108,14,-101,-27,94,-86,110,-28,-89,-49,-19,-96,-73,127,100,114,0,28,-118,32,104,27,-38,11,-89,-25,114,81,97,6,-34,-106,-18,17,-53,11,-96,-39,-106,55,33,55,-107,-39,19,-77,-94,102,-85,-88,-14,-126,40,-126,-84,109,34,-36,110,119,110,126,126,-66,-53,10,49,-117,-16,89,29,-66,-7,-51,111,-90,43,-118,18,-18,-30,8,67,61,-3,-93,-17,-110,28,62,-23,-25,7,79,109,-95,122,-17,-48,106,60,-34,-52,-13,-6,-121,-51,60,-4,-33,59,19,-110,19,22,-64,30,-124,-53,-55,76,-94,108,90,110,-62,123,-38,44,-101,5,-95,9,96,-54,-8,-12,-72,101,5,-95,64,-37,-80,36,84,85,77,-1,-42,-73,-66,21,-114,-32,-119,-120,31,-108,-106,-106,-106,2,120,-122,87,-92,25,99,-37,-75,-11,117,-10,115,-1,-81,-73,-79,-71,-26,88,-36,50,-1,-77,-59,-53,-65,61,-5,-73,-124,113,-92,33,-56,40,-126,4,80,-108,-101,-106,-48,-16,118,-10,6,34,-66,22,64,70,90,76,115,-94,-48,-35,23,-37,-7,14,-121,-61,-99,-101,59,52,35,68,8,82,85,-43,105,11,-89,70,-70,95,-114,-26,38,-37,112,-86,123,-128,31,-1,-10,111,-20,57,-44,30,-109,87,-67,-25,56,-21,-98,-37,49,-22,-112,-75,-61,48,-93,59,39,45,-39,-103,40,64,73,111,127,16,-35,-26,15,100,-90,-72,18,-106,-41,-93,59,63,124,106,-60,-31,118,-69,35,78,84,-124,32,77,-45,-50,75,-12,60,53,-55,73,-118,39,54,-128,-107,-30,113,-110,50,74,96,-21,124,-61,-91,-118,-124,26,23,-81,-13,117,93,-113,112,17,33,-88,-81,-81,79,-77,34,-121,96,119,-75,-57,-72,-124,55,37,76,41,-54,-28,-31,-43,-105,49,125,66,86,76,-2,-94,89,-7,-84,-69,-13,82,-58,103,-89,-116,-87,62,16,56,-44,-24,32,88,119,95,48,97,-36,45,37,-39,-123,-61,-74,38,-23,25,-44,19,-106,87,-44,-24,104,15,-128,105,-102,122,79,79,79,100,-20,41,-31,-116,67,-121,14,13,-102,-90,25,101,-75,-128,49,5,-46,77,9,115,-90,-115,-29,-87,-17,47,103,-2,-12,-68,-72,-27,-54,-25,23,-13,-24,119,-105,81,-112,-99,60,106,-99,114,-40,-77,-91,-108,28,111,-13,39,12,102,100,-89,-70,81,109,-115,-18,-18,13,36,44,-17,25,33,10,-87,-21,122,-32,-32,-63,-125,97,30,-122,-106,-11,-21,-41,-81,-17,49,77,51,60,79,70,52,40,35,-43,-99,-88,29,-104,18,46,41,29,-49,83,-107,87,48,-61,-90,57,-90,109,-16,-37,127,47,41,45,96,-35,-73,46,29,-107,36,41,-63,105,107,64,123,-41,-64,-88,-31,-40,-15,-29,-122,-76,-45,48,76,-114,-73,-9,37,40,13,-39,25,81,70,60,116,60,69,-45,-4,-65,-1,-3,-17,-69,-62,-119,-111,-59,106,123,123,-69,12,6,-125,62,-21,58,114,87,122,-118,43,-18,56,54,37,-52,-99,58,-114,-121,87,95,-54,-92,-126,-95,41,-75,-74,-23,20,91,-10,14,69,5,-73,-41,-73,-14,-26,-114,-95,21,-9,-107,-13,39,-16,-53,-69,-81,96,66,94,90,92,-31,117,41,73,-79,57,122,59,14,-8,-16,-74,-6,-29,-106,55,37,-108,-40,-68,-12,-10,-18,1,62,57,26,63,-122,101,74,-56,73,31,34,40,124,-24,-85,-65,-65,-65,3,24,90,-84,90,-121,45,21,-64,28,24,24,56,50,-68,-94,-84,52,79,-36,-103,96,90,113,6,-65,-8,-50,-46,40,114,14,28,-21,-30,-2,-89,-73,-47,104,115,-38,78,-98,-18,-25,-63,-1,-38,-50,-82,3,67,78,-37,-110,-39,5,60,-14,-99,-91,113,53,-55,-91,42,-92,-39,8,-38,89,-33,74,-94,-63,-18,113,59,-104,91,50,-28,-112,122,79,-10,112,-68,35,-127,6,9,-56,-53,74,-118,74,50,77,-109,-98,-98,-98,-29,-124,34,-87,-118,16,66,-38,23,-85,70,119,119,-9,-57,98,-104,85,27,-97,-109,-116,58,44,24,35,-127,105,19,50,121,120,-11,82,102,76,28,26,86,-11,-51,-89,120,-32,-23,15,56,120,-76,51,102,-20,-73,119,-10,-13,-48,-122,15,-87,25,70,-46,-38,59,47,37,63,43,-106,-92,-15,57,-55,20,100,-122,122,-40,-37,-46,-51,-37,53,71,99,-54,-40,113,-23,-20,2,102,-38,100,105,104,62,53,98,12,41,-116,-12,36,39,83,109,-114,-92,16,2,77,-45,56,125,-6,116,3,-95,-59,122,-52,98,-43,108,106,106,-38,107,21,-114,116,-42,-108,-126,116,50,83,-94,-41,52,18,-72,101,-59,12,22,-40,86,-26,31,-18,63,73,-27,-6,106,-10,123,-37,-29,26,-58,-93,-83,126,-22,-68,-47,62,-46,-84,73,-39,-28,14,-21,73,-128,-55,5,-23,-111,-99,-48,-9,118,31,-25,-28,-87,-2,-104,50,97,-88,-86,-62,-115,-53,-90,-30,-74,25,-11,-113,62,105,75,104,-96,11,-58,-91,70,-126,-4,82,74,20,69,81,-125,-63,32,-121,14,29,-38,7,-104,85,85,85,67,49,103,-84,-109,-93,-49,61,-9,92,93,48,24,108,-79,-91,81,-108,-105,74,81,94,-76,-53,-82,0,-65,121,-83,46,-78,-5,-80,-23,-93,35,-36,-5,-85,-83,28,109,-21,-119,43,-112,67,85,-72,-21,-58,50,110,-67,118,86,36,-83,-27,84,31,63,-36,-80,-99,90,111,116,-72,67,2,23,21,101,-32,-80,102,-92,-93,-83,-2,-124,-38,-80,120,102,62,43,22,12,69,17,-21,-102,78,81,115,-96,45,-2,13,-64,-100,-110,108,-5,4,100,2,-8,-3,-2,-42,-105,95,126,121,31,96,86,84,84,-104,-31,-74,34,-124,-112,82,74,-15,-4,-13,-49,119,-7,-3,-2,26,0,-21,48,38,-55,30,39,101,35,4,-62,-38,78,-11,-79,-18,15,31,-15,-20,95,-22,121,-24,-39,-19,-8,-70,-30,-9,-80,-57,-87,114,-49,87,-54,-8,-50,-115,101,56,28,-95,70,123,91,-70,-71,-25,-55,-9,-87,-34,27,-69,91,98,-104,-110,-39,-74,-128,-41,-16,97,111,-121,-86,8,110,94,49,61,124,-126,3,73,40,-122,-99,104,57,35,-127,-59,-77,10,-94,-110,12,-61,-96,-83,-83,-83,-10,-99,119,-34,105,-77,34,-84,81,26,4,-95,49,-89,121,-67,-34,-73,-121,11,-11,-71,105,-29,70,84,87,-17,-119,110,30,-7,-29,46,58,-3,49,-18,83,20,-106,127,-82,-112,-69,111,26,34,-25,72,-85,-97,-5,126,-67,-115,-35,13,35,111,97,103,-92,-72,-104,51,121,-56,-32,-50,-98,-100,51,98,-88,67,2,-53,-25,21,-77,98,-63,-124,72,-38,126,111,7,-101,118,38,-74,87,89,-23,30,22,-50,28,50,15,-86,-86,50,56,56,72,99,99,-29,123,-128,86,93,93,29,-21,73,19,50,76,-38,99,-113,61,-10,86,32,16,104,-57,-118,-119,0,-84,-104,63,-127,-119,5,-79,43,99,-63,-40,94,41,72,75,118,69,-74,-122,-9,123,59,-72,-13,-25,-101,-39,119,-88,61,110,-100,105,-23,-100,-15,-52,-100,56,116,36,102,-43,-107,-45,-8,-18,77,-97,67,-75,-35,32,-127,47,95,54,-103,71,-17,-70,60,-78,55,-17,-17,11,-14,-45,-25,118,-46,-23,31,24,94,101,20,46,-97,51,62,18,-72,-109,82,-102,-128,-38,-43,-43,-43,-15,-69,-33,-3,-18,13,64,43,47,47,-113,-84,-110,35,4,9,33,-28,-82,93,-69,-88,-86,-86,106,111,105,105,121,-45,-70,89,2,100,-90,-71,89,52,35,54,110,124,-90,56,116,-84,-117,-5,-97,-2,-128,-61,39,-29,-5,51,18,-104,55,61,47,74,-125,-123,16,-36,117,-29,92,-2,-19,-21,75,-104,55,35,-97,-55,-59,89,124,-11,-118,105,-84,-71,-29,18,-46,109,-114,-20,-101,59,-114,-80,39,78,-4,39,12,67,-122,-126,111,-74,-70,-91,-90,105,52,53,53,-67,-73,105,-45,-90,99,-69,118,-19,-118,122,63,45,106,-11,-72,96,-63,2,19,24,124,-13,-51,55,95,88,-67,122,-11,-19,14,-121,67,-75,72,18,-41,44,-102,-64,-1,108,-13,98,-114,49,-4,49,28,7,-113,117,-14,-32,111,-74,115,-16,88,-20,14,-119,29,-123,-29,82,88,49,127,66,76,122,-110,-37,-63,109,-97,-97,-55,-86,-85,46,66,-45,77,82,-122,-19,90,52,30,-17,-30,-39,49,28,-95,-103,60,62,-99,-27,-13,-118,-128,-112,2,-88,-86,-86,118,117,117,-79,121,-13,-26,63,1,-125,77,77,77,81,13,-116,90,-83,9,33,-116,53,107,-42,-24,119,-33,125,-9,-18,-106,-106,-106,119,45,-17,-46,4,88,86,86,-60,-43,35,8,62,22,-20,62,-40,78,-27,19,-17,-77,-17,80,-30,-34,21,66,112,-53,-43,51,-103,90,20,127,-113,-35,-27,84,99,-56,9,106,6,-113,-3,105,15,-115,-57,-69,-30,-36,21,-126,9,-4,-61,53,51,-19,27,0,-90,105,-102,52,54,54,110,91,-69,118,-19,7,107,-42,-84,-47,87,-83,90,21,117,114,34,-26,116,-57,-113,127,-4,99,3,-24,-37,-72,113,-29,-81,53,77,67,81,20,21,-112,110,-105,74,-27,-54,121,81,94,-13,88,-48,112,-76,-109,31,-4,-57,22,-68,-93,8,47,-127,-21,47,41,9,-19,-72,-98,33,106,14,-74,83,-67,-9,-8,-88,39,82,-105,-52,46,-96,-94,124,90,-24,121,-106,-10,116,118,118,-14,-41,-65,-2,-11,25,-96,111,-10,-20,-39,49,-57,74,98,8,18,66,24,-107,-107,-107,-63,123,-18,-71,-25,-61,-90,-90,-90,87,76,-45,68,74,105,0,-52,-102,-108,-59,61,43,-25,-115,122,22,39,-116,-99,-97,-76,81,-7,120,53,39,124,-15,-3,-93,48,-26,79,-49,-29,-95,-81,47,38,61,37,-15,-74,-15,72,-88,109,58,53,-22,25,73,-105,83,-27,-114,-21,75,-55,-80,-22,23,66,24,-102,-90,81,95,95,-1,-41,-97,-4,-28,39,-17,86,86,86,6,-121,107,15,-60,57,97,-10,-28,-109,79,106,64,-49,-38,-75,107,-1,-67,-89,-89,-89,91,81,20,7,-42,-116,118,-3,37,37,84,126,117,30,-98,81,-126,95,-11,77,29,-36,-1,-12,54,-102,79,-114,28,100,-73,-93,48,55,-107,7,110,93,72,110,102,-84,71,61,22,-116,-76,109,100,-121,4,-66,-70,124,26,-27,67,-74,-57,84,20,-59,113,-14,-28,73,-1,-29,-113,63,-2,-17,64,79,78,78,-50,-120,-107,-116,72,-112,16,-62,124,-14,-55,39,-125,47,-68,-16,66,83,117,117,-11,-6,-63,-63,-63,-120,101,119,58,20,86,127,-87,-108,-89,-66,87,-50,-126,25,-7,113,15,32,-68,-66,-3,112,66,-49,26,66,-34,-11,23,-106,-108,-16,-52,-67,43,-94,-106,45,103,-118,-4,-100,-8,65,56,85,85,-8,-38,-118,25,-4,-13,-51,23,-29,116,40,-31,101,-123,-20,-18,-18,102,-13,-26,-51,-1,-7,-54,43,-81,52,84,86,86,6,-41,-84,89,51,-30,-20,-109,104,-40,-118,-14,-14,-14,-108,-22,-22,-22,-68,125,-5,-10,-3,-25,-84,89,-77,62,-81,-86,106,-44,25,-23,-50,-98,0,127,-34,-46,72,85,117,35,-121,-114,117,-111,56,-64,25,-126,41,33,-39,-29,96,-15,-84,2,110,90,54,-123,107,23,79,-118,28,-47,61,91,116,116,15,112,-41,47,-33,-93,-26,-96,111,88,-125,66,94,-10,-113,-66,-66,24,-73,75,13,-109,-93,-21,-70,-18,-40,-66,125,-5,59,-53,-105,47,95,93,94,94,-18,-85,-82,-82,-18,-125,-111,-123,79,104,-41,94,124,-15,69,117,-43,-86,85,-23,121,121,121,19,119,-20,-40,-15,-14,-124,9,19,-90,-122,-49,25,-37,-53,-99,-22,30,100,-49,-95,118,14,29,-17,-94,-7,-92,-97,-114,-18,1,2,1,-115,-96,102,-96,8,-127,-53,-23,-64,-29,113,80,-112,-99,-52,-12,-119,89,124,110,-22,56,102,78,-54,-62,-27,56,55,98,-20,56,-46,-42,-61,51,-81,-18,-25,-61,-70,22,6,3,6,69,121,-87,124,97,73,9,95,-69,-22,-94,72,-40,68,74,-87,43,-118,-30,-88,-81,-81,111,-66,-14,-54,43,87,118,116,116,52,87,84,84,-8,-85,-86,-86,-50,-2,-11,-51,111,124,-29,27,30,32,-1,-10,-37,111,-65,-66,-75,-75,-75,91,74,41,117,93,-41,13,-61,-112,-119,62,-70,110,-56,-63,-128,38,3,65,45,97,-71,-13,-7,-47,117,93,-74,-99,-22,-107,71,78,118,-55,-34,-2,-64,-16,60,93,74,41,-101,-101,-101,-69,111,-66,-7,-26,27,-128,124,-85,109,-25,-114,-118,-118,-118,84,-96,120,-19,-38,-75,119,116,116,116,-24,99,37,-23,-17,-27,19,38,-25,-60,-119,19,-6,3,15,60,-80,26,-104,96,-75,-23,-68,-67,56,34,42,42,42,50,-128,-110,71,31,125,-76,-46,-25,-13,-23,82,74,105,24,-122,-90,-21,-6,103,78,64,34,-115,-46,117,93,11,-109,-77,110,-35,-70,31,0,37,87,95,125,117,-58,-7,36,39,12,-91,-68,-68,60,19,-104,-14,-48,67,15,-35,121,-12,-24,-47,110,-61,48,-92,105,-102,127,-105,36,-23,-70,46,77,-45,-44,76,-45,-108,77,77,77,-35,-9,-35,119,-33,106,96,-54,-68,121,-13,50,-71,-128,111,70,42,-105,95,126,121,22,48,121,-43,-86,85,95,106,104,104,104,28,28,28,-76,-115,-72,-49,-98,24,-117,28,67,74,-87,7,2,1,89,91,91,-21,93,-71,114,-27,13,-64,100,75,-10,11,-2,-38,104,88,-109,38,37,39,39,47,-36,-70,117,-21,91,-99,-99,-99,-46,54,-28,-52,-49,-112,24,-45,48,12,77,74,41,59,59,59,-27,-69,-17,-66,-5,118,82,82,-46,98,96,-110,37,-13,-89,-10,78,-83,88,-70,116,105,26,80,4,-52,-39,-80,97,-61,-49,14,31,62,-36,25,8,4,62,19,-94,-20,-60,4,2,1,-39,-36,-36,-36,-7,-85,95,-3,-22,97,96,14,80,100,-55,-6,-87,-2,-45,13,-128,40,41,41,-15,-108,-108,-108,20,0,-45,-105,47,95,126,-51,-26,-51,-101,95,-13,-7,124,82,-45,-76,48,81,23,116,-24,89,117,-121,103,85,-23,-13,-7,-28,91,111,-67,-11,-105,101,-53,-106,125,30,-104,94,82,82,82,80,82,82,-30,-7,44,-56,-79,-61,49,119,-18,-36,44,96,34,80,-6,-3,-17,127,-1,27,-37,-74,109,123,-33,-25,-13,-23,-106,125,-78,-72,10,-111,117,46,-102,-91,-21,-70,105,-43,-95,75,41,-115,-80,-58,-8,124,62,125,-37,-74,109,-17,87,86,86,-34,1,-108,2,19,45,-103,-50,-7,-76,-60,-7,98,86,-108,-108,-108,-72,85,85,77,-9,122,-67,-87,64,-54,29,119,-36,81,-74,114,-27,-54,27,102,-50,-100,-71,44,39,39,-89,32,57,57,25,-121,35,36,-81,-43,56,-84,-29,-58,-31,32,87,-52,-97,-101,-124,-13,-62,-89,-65,-62,-37,81,-70,-82,51,48,48,64,71,71,71,107,67,67,-61,-42,-105,94,122,-23,-43,-33,-2,-10,-73,-75,64,-33,-44,-87,83,123,13,-61,-16,31,62,124,56,64,-100,-27,-61,25,53,-20,92,43,24,6,-75,-76,-76,52,-87,-81,-81,47,-11,-16,-31,-61,-55,64,-46,-76,105,-45,114,43,43,43,-105,-107,-107,-107,45,45,46,46,46,-51,-50,-50,46,118,-69,-35,-72,92,46,84,85,69,81,18,-37,77,41,37,-70,-82,-93,105,26,-127,64,-128,-45,-89,79,31,63,113,-30,68,-3,-34,-67,123,63,120,-22,-87,-89,-74,54,54,54,-74,3,3,37,37,37,-3,41,41,41,-67,-11,-11,-11,3,-40,78,-23,-98,43,46,-44,-40,84,-53,-54,-54,60,64,74,109,109,-83,27,72,6,92,-123,-123,-123,25,21,21,21,51,-26,-51,-101,55,-93,-88,-88,104,106,118,118,118,97,74,74,74,118,82,82,82,-102,-61,-31,112,59,44,21,-45,67,8,4,2,1,127,79,79,79,103,87,87,-41,-119,99,-57,-114,53,-19,-35,-69,-9,64,85,85,-43,-127,-106,-106,-106,110,-84,127,-87,42,43,43,27,4,-6,106,107,107,7,57,-113,-60,-124,113,-95,-115,-105,82,90,90,-22,-16,120,60,73,93,93,93,110,-81,-41,-21,2,-36,-124,-34,125,117,-124,63,-123,-123,-123,-98,-62,-62,-62,-92,-108,-108,20,7,64,95,95,-97,-34,-46,-46,50,-48,-46,-46,50,72,104,-97,60,-4,-47,-128,-64,-44,-87,83,-125,-103,-103,-103,-127,-63,-63,-63,-127,-6,-6,122,-99,17,-34,-13,58,95,-8,52,-83,-69,-78,96,-63,2,85,-45,52,-105,-94,40,78,33,-124,-13,-60,-119,19,-118,-49,-25,83,24,-6,-33,-113,-80,60,18,-21,-1,55,-14,-14,-14,-52,-94,-94,34,83,74,-87,-103,-90,-87,57,-99,-50,96,77,77,-115,-63,5,36,-59,-114,-49,114,-6,19,88,39,-38,-37,-37,-37,69,110,110,-82,-46,-37,-37,43,0,82,83,83,101,123,123,-69,-103,-101,-101,43,-83,61,114,-5,-65,81,125,-86,-8,-1,-106,-71,106,-21,-8,85,81,57,0,0,0,0,73,69,78,68,-82,66,96,-126};
    DogsData data1 = new DogsData("puppy","hm10","46233853494697469");
    DogsData data2 = new DogsData("rexy",true , outImage,"hm10","534645776756856876");
    DogsData data3 = new DogsData("doggy","hm10","46233853236943760");
    DogsData data4 = new DogsData("rex","hm10","4623385349445678983");*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();



        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    private void getDogListFromDB() {
        ArrayList<DogsData> persistList = daf.getAllDogProfile();
        if(persistList.size()>0){
            for(DogsData dd : persistList){
                mLeDeviceListAdapter.addDevice(dd);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                 mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        mLeDeviceListAdapter.addDevice(null);
        mLeDeviceListAdapter.notifyDataSetChanged();
        getDogListFromDB();
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(position == 0){
            final Intent intent = new Intent(this, DeviceScanActivity.class);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }else {
            final DogsData device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;
            final Intent intent = new Intent(this, DogProfileControlActivity.class);
            intent.putExtra(DogProfileControlActivity.EXTRAS_DEVICE_NAME, device.getDeviceName());
            intent.putExtra(DogProfileControlActivity.EXTRAS_DEVICE_ADDRESS, device.getDeviceAddress());
            intent.putExtra(DogProfileControlActivity.EXTRAS_DOG_ID, device.getId()+"");
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<DogsData> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<DogsData>();
            mInflator = DogScanActivity.this.getLayoutInflater();
        }

        public void addDevice(DogsData device) {
            if(!mLeDevices.contains(device)) {
                if(device != null)
                device.setStatus(false);
                mLeDevices.add(device);
            }
        }

        public void invalidateDevice() {
            for(DogsData dgsData : mLeDevices){
                if(dgsData != null )
                    dgsData.setStatus(false);
            }
            notifyDataSetChanged();
        }

        public void updateDevice(BluetoothDevice device) {
            String addrss = device.getAddress();
            for(DogsData dgsData : mLeDevices){
                if(dgsData != null )
                if(dgsData.getDeviceAddress().equals(addrss)){
                    dgsData.setStatus(true);
                }
            }
        }

        public DogsData getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            invalidateDevice();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if(i == 0){
                if (view == null) {
                    view = mInflator.inflate(R.layout.add_item, null);

                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                return view;
            }else {
                if (view == null) {
                    view = mInflator.inflate(R.layout.listitem_dog, null);
                    viewHolder = new ViewHolder();
                    //viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                    viewHolder.dogName = (TextView) view.findViewById(R.id.dog_name);
                    viewHolder.dogAge = (TextView) view.findViewById(R.id.dog_age);
                    viewHolder.dogGoal= (TextView) view.findViewById(R.id.dog_goal);
                    viewHolder.dogImage = (ImageView) view.findViewById(R.id.dog_image);
                    viewHolder.bluetooth = (ImageView) view.findViewById(R.id.ble_image);

                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                DogsData dogsData = mLeDevices.get(i);
                final String dogsDataName = dogsData.getName();
                final int dogsDataAge = dogsData.getAge();
                final String dogsDataGoal = dogsData.getGoal();
                if (dogsDataName != null && dogsDataName.length() > 0)
                    viewHolder.dogName.setText(dogsDataName);
                else
                    viewHolder.dogName.setText(R.string.unknown_device);

                if (dogsDataAge != 0)
                    viewHolder.dogAge.setText(dogsDataAge+"");
                else
                    viewHolder.dogAge.setText("0");

                if (dogsDataGoal != null && dogsDataGoal.length() > 0)
                    viewHolder.dogGoal.setText(dogsDataGoal);
                else
                    viewHolder.dogGoal.setText("");
                if(dogsData.getImage() !=  null){
                    byte[] outImage=dogsData.getImage();
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    viewHolder.dogImage.setImageBitmap(theImage);
                }else{
                    viewHolder.dogImage.setImageResource(R.drawable.pack_btn);
                }

                if(dogsData.isStatus()){
                    viewHolder.bluetooth.setImageResource(R.drawable.bluetooth);
                }else{
                    viewHolder.bluetooth.setImageResource(R.drawable.discnt);
                }

                return view;
            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.updateDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView dogName;
        TextView dogAge;
        TextView dogGoal;
        ImageView dogImage;
        ImageView bluetooth;
    }
}