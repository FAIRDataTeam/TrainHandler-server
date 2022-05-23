/**
 * The MIT License
 * Copyright © 2022 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatatrain.trainhandler.service.async;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JobNotificationListener {

    private final Map<UUID, Object> locks = new HashMap<>();

    @EventListener
    public void handleJobEventNotification(JobNotification notification) {
        if (locks.containsKey(notification.getJobUuid())) {
            synchronized (locks.get(notification.getJobUuid())) {
                final Object lock = locks.remove(notification.getJobUuid());
                lock.notifyAll();
            }
        }
    }

    private void prepare(UUID jobUuid) {
        if (!locks.containsKey(jobUuid)) {
            locks.put(jobUuid, new Object());
        }
    }

    public void wait(UUID jobUuid) throws InterruptedException {
        prepare(jobUuid);
        synchronized (locks.get(jobUuid)) {
            locks.get(jobUuid).wait();
        }
    }
}
