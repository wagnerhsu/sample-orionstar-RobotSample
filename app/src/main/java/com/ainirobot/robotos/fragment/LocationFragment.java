/*
 *  Copyright (C) 2017 OrionStar Technology Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ainirobot.robotos.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.robotos.LogTools;
import com.ainirobot.robotos.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationFragment extends BaseFragment {

    private static final String TAG = "LocationFragment";
    private double mCurrentX;
    private double mCurrentY;
    private double mCurrentTheta;

    private Button mIs_location;
    private Button mGet_location;
    private Button mSet_location;
    private Button mIs_in_location;
    private Button mRemove_location;
    private Button mSet_reception_point;
    private Button mGetname;
    private Button mGet_all_maps;
    private Button mFindCurrentPointBtn;
    private EditText mEditPlaceName;

    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.fragment_location_layout, null, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mIs_location = (Button) root.findViewById(R.id.is_location);
        mGet_location = (Button) root.findViewById(R.id.get_location);
        mSet_location = (Button) root.findViewById(R.id.set_location);
        mIs_in_location = (Button) root.findViewById(R.id.is_in_location);
        mRemove_location = (Button) root.findViewById(R.id.remove_location);
        mSet_reception_point = (Button) root.findViewById(R.id.set_reception_point);
        mGetname = (Button) root.findViewById(R.id.getname);
        mGet_all_maps = (Button) root.findViewById(R.id.get_all_maps);
        mEditPlaceName = (EditText) root.findViewById(R.id.edit_place_name);
        mFindCurrentPointBtn = new Button(root.getContext());
        mFindCurrentPointBtn.setText("查找当前点节点名");
        ((android.widget.LinearLayout) root).addView(mFindCurrentPointBtn);


        mIs_in_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRobotInlocation();
            }
        });

        mSet_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPostEstimate();
            }
        });

        mIs_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRobotEstimate();
            }
        });

        mSet_reception_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        mGet_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        mRemove_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocation();
            }
        });

        mGetname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getName();
            }
        });
        mGet_all_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllMaps();
            }
        });
        mFindCurrentPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentPointNodeName();
            }
        });
    }


    /**
     * is robot in location
     * 判断机器人是否在位置点
     */
    private void isRobotInlocation() {
        try {
            String placeName = mEditPlaceName.getText() != null ? mEditPlaceName.getText().toString().trim() : "";
            if (TextUtils.isEmpty(placeName)) {
                LogTools.info("Place name is empty, please input a name");
                return;
            }
            JSONObject params = new JSONObject();
            params.put(Definition.JSON_NAVI_TARGET_PLACE_NAME, placeName);
            params.put(Definition.JSON_NAVI_COORDINATE_DEVIATION, 2.0);

            RobotApi.getInstance().isRobotInlocations(0,
                    params.toString(), new CommandListener() {
                        @Override
                        public void onResult(int result, String message) {
                            try {
                                JSONObject json = new JSONObject(message);
                                json.getBoolean(Definition.JSON_NAVI_IS_IN_LOCATION);
                                LogTools.info("isRobotInlocation result: " + result + " message: "+ message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * set robot in init estimate
     * 设置机器人初始坐标点
     */
    private void setPostEstimate() {
        if(mCurrentX == 0 || mCurrentY == 0){
            LogTools.info("Estimate is empty, please set it before use");
            LogTools.info("坐标为空,请先获取当前坐标");
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put(Definition.JSON_NAVI_POSITION_X, mCurrentX);
            params.put(Definition.JSON_NAVI_POSITION_Y, mCurrentY);
            params.put(Definition.JSON_NAVI_POSITION_THETA, mCurrentTheta);

            RobotApi.getInstance().setPoseEstimate(0, params.toString(), new CommandListener() {
                @Override
                public void onResult(int result, String message) {
                    LogTools.info("setPostEstimate result: " + result + " message: "+ message);
                    if ("succeed".equals(message)) {
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * is robot
     * 判断当前是否已定位
     */

    private void getName(){
        RobotApi.getInstance().getMapName(0,new CommandListener(){
            @Override
            public void onResult(int result, String message, String extraData) {
                super.onResult(result, message, extraData);
                if (!TextUtils.isEmpty(message)) {
                    String name = message;
                    LogTools.info(" name: ----" + name+"==="+message);
                }
            }
        });
    }


    /**
     * is robot
     * 判断当前是否已定位
     */
    private void isRobotEstimate() {
        RobotApi.getInstance().isRobotEstimate(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("isRobotEstimate result: " + result + " message: " + message);
                if (!"true".equals(message)) {
                } else {
                }
            }
        });
    }

    /**
     * 设置当前位置名称
     */
    private void setLocation(){
        String placeName = mEditPlaceName.getText() != null ? mEditPlaceName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(placeName)) {
            LogTools.info("Place name is empty, please input a name");
            return;
        }
        RobotApi.getInstance().setLocation(0, placeName, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("setLocation result: " + result + " message: " + message);
                if ("succeed".equals(message)) {
                } else {
                }
            }
        });
    }

    /**
     * 获取当前坐标点
     */
    private void getLocation(){
        RobotApi.getInstance().getPosition(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("getLocation result: " + result + " message: "+ message);
                Toast.makeText(getContext(), "getLocation result: " + result + " message: "+ message, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(message);
                    mCurrentX = json.getDouble(Definition.JSON_NAVI_POSITION_X);
                    mCurrentY = json.getDouble(Definition.JSON_NAVI_POSITION_Y);
                    mCurrentTheta = json.getDouble(Definition.JSON_NAVI_POSITION_THETA);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 删除位置点
     */
    private void removeLocation(){
        RobotApi.getInstance().removeLocation(0, "接待点", new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("removeLocation result: " + result + " message: "+ message);
                if ("succeed".equals(message)) {
                } else {
                }
            }
        });
    }

    /**
     * 获取所有地图列表
     */
    private void getAllMaps() {
        RobotApi.getInstance().getPlaceList(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("getAllMaps result: " + result + " message: " + message);
                // 这里可以根据需要进一步处理 message，比如解析 JSON 或显示到界面
            }
        });
    }

    /**
     * 查找当前点是否在地图节点上，返回节点名或空字符串（用API判断，异步串行，Deviation=2.0）
     */
    private void findCurrentPointNodeName() {
        RobotApi.getInstance().getPosition(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                if (result == 0 || TextUtils.isEmpty(message)) {
                    LogTools.info("获取当前点失败");
                    Toast.makeText(getContext(), "获取当前点失败", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject json = new JSONObject(message);
                    // 获取所有点位
                    RobotApi.getInstance().getPlaceList(0, new CommandListener() {
                        @Override
                        public void onResult(int result, String message) {
                            if (result == 0 || TextUtils.isEmpty(message)) {
                                LogTools.info("获取地图点位失败");
                                Toast.makeText(getContext(), "获取地图点位失败", Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                org.json.JSONArray arr = new org.json.JSONArray(message);
                                checkNodeAtIndex(arr, 0);
                            } catch (Exception e) {
                                LogTools.info("解析点位失败: " + e.getMessage());
                                Toast.makeText(getContext(), "解析点位失败", Toast.LENGTH_LONG).show();
                            }
                        }
                        // 串行异步检查每个节点
                        private void checkNodeAtIndex(final org.json.JSONArray arr, final int idx) {
                            if (idx >= arr.length()) {
                                Toast.makeText(getContext(), "当前点不在任何节点上", Toast.LENGTH_LONG).show();
                                LogTools.info("当前点节点名: ");
                                return;
                            }
                            try {
                                JSONObject obj = arr.getJSONObject(idx);
                                final String name = obj.optString("name", "");
                                JSONObject params = new JSONObject();
                                params.put(Definition.JSON_NAVI_TARGET_PLACE_NAME, name);
                                params.put(Definition.JSON_NAVI_COORDINATE_DEVIATION, 2.0); // 与isRobotInlocation一致
                                RobotApi.getInstance().isRobotInlocations(0, params.toString(), new CommandListener() {
                                    @Override
                                    public void onResult(int result, String message) {
                                        try {
                                            JSONObject json = new JSONObject(message);
                                            boolean inLoc = json.optBoolean(Definition.JSON_NAVI_IS_IN_LOCATION, false);
                                            if (inLoc) {
                                                Toast.makeText(getContext(), name, Toast.LENGTH_LONG).show();
                                                LogTools.info("当前点节点名: " + name);
                                            } else {
                                                checkNodeAtIndex(arr, idx + 1);
                                            }
                                        } catch (JSONException e) {
                                            checkNodeAtIndex(arr, idx + 1);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                checkNodeAtIndex(arr, idx + 1);
                            }
                        }
                    });
                } catch (JSONException e) {
                    LogTools.info("解析当前点失败: " + e.getMessage());
                }
            }
        });
    }

    public static Fragment newInstance() {
        return new LocationFragment();
    }
}
