package com.krld.vkresearch.views;

import android.content.*;
import android.view.*;
import android.graphics.*;
import com.krld.vkresearch.model.User;
import com.krld.vkresearch.model.UserGraphContext;
import com.krld.vkresearch.model.UserLink;

import java.util.*;

public class GraphView extends View implements UserGraphContext {

    private Set<User> users;

    private Set<UserLink> userLinks;

    private int maxDistanceChild = 0;
    private boolean showForceFields = false;

    @Override
    public User getRoot() {
        return root;
    }

    @Override
    public Set<User> getAllUsers() {
        return users;
    }

    @Override
    public Set<UserLink> getAllUserLinks() {
        return userLinks;
    }


    private float canvasScale = 0f;

    private User root;

    private static final double RANDOM_SPREAD = 300;

    private Thread updater;

    private com.krld.vkresearch.model.Point clickPoint;

    public GraphView(Context context) {
        super(context);
        initData();
        updater = new Thread(new Runnable() {

            private static final long DELAY = 50;

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                    }
                    update();
                    postInvalidate();
                }
            }


        });
        updater.start();
    }

    private void initData() {
        //initViaHttp();
        initRandomData();
    }

    private void update() {
        if (clickPoint != null) {
            root.pos = clickPoint;
            clickPoint = null;

        }
        for (User user : users) {
            if (user != root)
                user.updatePos();
        }
    }

    private void initRandomData() {
        root = new User(-1, "корень", "корень");
        root.setContext(this);
        users = new HashSet<User>();
        userLinks = new HashSet<UserLink>();

        users.add(root);
        int count = 50;

        for (int i = 0; i < count; i++) {
            User tmpUser = new User(i, "ветвь", "ветвь");
            tmpUser.setContext(this);
            tmpUser.pos.x = root.pos.x + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.pos.y = root.pos.y + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.friends.add(root);

            root.friends.add(tmpUser);
            userLinks.add(new UserLink(tmpUser, root));
            users.add(tmpUser);
        }

        User anotherRoot = getUserById((long) (Math.random() * count));

        for (int i = count; i < count * 2; i++) {
            User tmpUser = new User(i, "ветвь", "ветвь");
            tmpUser.setContext(this);
            tmpUser.pos.x = anotherRoot.pos.x + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.pos.y = anotherRoot.pos.y + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.friends.add(anotherRoot);

            anotherRoot.friends.add(tmpUser);
            userLinks.add(new UserLink(tmpUser, anotherRoot));
            users.add(tmpUser);
        }




        for (int i = 0; i < count * 0.1; i++) {
            User rndUser = getUserById((long) (Math.random() * count));

            User rndUser2 = getUserById((long) (Math.random() * count));
            if (rndUser.equals(rndUser2)) continue;
            if (rndUser == root || rndUser2 == root) continue;
            maxDistanceChild = 100;
            rndUser.maxDistance = maxDistanceChild;

            rndUser2.maxDistance = maxDistanceChild;

            rndUser2.friends.add(rndUser);

            rndUser.friends.add(rndUser2);
            userLinks.add(new UserLink(rndUser2, rndUser));
            //	users.add(rndUser2);
        }

         /*


        for (int i = count; i < count * 1.42; i++) {
            User rndUser = getUserById((long) (Math.random() * count));

            rndUser.maxDistance = 80;
            User tmpUser = new User(i, "ветвь", "ветвь");
            tmpUser.setContext(this);
            tmpUser.pos.x = rndUser.pos.x + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.pos.y = rndUser.pos.y + Math.random() * RANDOM_SPREAD - RANDOM_SPREAD / 2;

            tmpUser.friends.add(rndUser);

            rndUser.friends.add(tmpUser);
            userLinks.add(new UserLink(tmpUser, rndUser));
            users.add(tmpUser);
        }  */
    }

    private User getUserById(long userId) {
        for (User user : users) {
            if (user.userId == userId) {
                return user;
            }
        }
        return null;
    }

    private void initViaHttp() {
        // TODO: Implement this method
    }

    @Override
    public void onDraw(Canvas canvas) {
        fitCanvas(canvas);
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        drawUsers(canvas, paint);
    }

    private void drawUsers(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);

        for (UserLink link : userLinks) {
            Iterator<User> iterator = link.link.iterator();
            com.krld.vkresearch.model.Point firstPos = iterator.next().pos;
            com.krld.vkresearch.model.Point secondPos = iterator.next().pos;

            canvas.drawLine(firstPos.xi(), firstPos.yi(), secondPos.xi(), secondPos.yi(), paint);
        }
        paint.setColor(Color.GREEN);
        final int sizeRoot = 10;
        final int sizeChild = 5;
        canvas.drawRect(root.pos.xi() - sizeRoot, root.pos.yi() - sizeRoot,
                root.pos.xi() + sizeRoot, root.pos.yi() + sizeRoot, paint);
        paint.setColor(Color.BLUE);
        for (User user : users) {
            if (showForceFields) {
                paint.setAlpha(10);
                canvas.drawCircle(user.pos.xi(), user.pos.yi(), (int) user.maxDistance, paint);
                paint.setColor(Color.RED);
                paint.setAlpha(10);
                canvas.drawCircle(user.pos.xi(), user.pos.yi(), (int) user.minDistance, paint);
            }
            paint.setColor(Color.BLUE);
            paint.setAlpha(255);
            canvas.drawCircle(user.pos.xi(), user.pos.yi(), sizeChild, paint);
        }

    }

    private void drawBackground(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setAlpha(200);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setAlpha(255);
    }

    private void fitCanvas(Canvas canvas) {
        if (canvasScale == 0f)
            canvasScale = canvas.getWidth() / 768f;
        if (canvasScale != 1f)
            canvas.scale(canvasScale, canvasScale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickPoint = new com.krld.vkresearch.model.Point(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }
}
