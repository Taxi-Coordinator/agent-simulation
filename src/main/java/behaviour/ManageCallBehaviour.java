package behaviour;

import agents.TaxiCoordinator;
import city.*;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.misc.Activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;

/**
 * This class Handle the Call Generation and send the request to taxis for auction
 */
public class ManageCallBehaviour extends OneShotBehaviour {
    private AID bestTaxi;     // The agent who provides the best offer
    private double bestPrice;     // The best offered price
    private int repliesCnt = 0;     // The counter of replies from seller agents
    private MessageTemplate mt;     // The template to receive replies
    private Activity activity = Activity.WAITING_FOR_CALLS;
    private final TaxiCoordinator agent;
    private Request lastBestRequest;
    private ArrayList<Stats> lstStats;
    private final ArrayList<Request> biddingList = new ArrayList<>();

    public ManageCallBehaviour(TaxiCoordinator coordinator) {
        agent = coordinator;
        lstStats = new ArrayList<Stats>(0);

    }

    private void nextCall() {
        agent.nextTime = agent.nextCall(agent.runtime.getDate());
    }

    public int daysBetween(Date day1, Date day2) {
        Calendar dayOne = Calendar.getInstance();
        dayOne.setTime(day1);

        Calendar dayTwo = Calendar.getInstance();
        dayTwo.setTime(day2);

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public void action() {
        Date initial = agent.runtime.getDate();
        for (int t = 0; daysBetween(initial, agent.runtime.getDate()) <= 8; t++) {
            agent.runtime.tick();
//            try {
//                Thread.sleep(1);
//            } catch (Exception e) {
//            }

            // 2 . Waiting for next call
            if (activity == Activity.WAITING_FOR_CALLS) {
                if (agent.isCallAvailable(agent.nextTime, agent.runtime.getDate())) {
                    // 3. Pick Random Node but not taxi center
                    int[] exclude = {agent.vCity.taxiCenter};
                    int nextIndex = agent.pickRandomIntersectionIndex(agent.vCity.intersections, exclude);
                    Intersection intersection = agent.vCity.intersections.get(nextIndex);

                    // 4. Receive call
                    System.out.println("---------------------------------------------------------------------------------------");
                    Passenger p = new Passenger(intersection, agent.calls++);
                    agent.vCity.totalPassengers++;
                    City.last_req_distance = p.d;
                    agent.receiveCall(p, intersection);
                    // 5. DO ACTION PROCESS HERE

                    // Pick random destination
                    int[] exclude2 = {agent.vCity.taxiCenter, nextIndex};
                    int destination = agent.pickRandomDropoffIndex(agent.vCity.dropoffPoints, exclude2);

                    //System.out.println("("+agent.runtime.toString()+")(Call " + agent.calls + ")");
                    System.out.println("(" + agent.runtime.toString() + ")  Calling from Node " + intersection.index + " to " + destination);
                    agent.out("Call " + intersection.index);

                    // Send Request to available taxi
                    agent.lastRequest = new Request(agent.vCity.intersections.get(nextIndex), new DropoffPoint(agent.vCity.dropoffPoints.get(destination).index), agent.calls);
                    sentRequest();

                    // 6. Set next Time to call. ONly if step is 0 that means that is waiting for call
                    if (activity == Activity.WAITING_FOR_CALLS) {
                        nextCall();
                    }

                }
            } else {
                sentRequest();
            }
        }

        Collections.sort(lstStats);
        System.out.println("Stats by Agent");
        System.out.println(System.out.format("| %10s | %15s | %15s | %15s | %20s | %20s | %20s |",
                "Agent", "Passengers", "Profit", "ProfitCompany", "MinBid", "MaxBid", "Shift"));
        double tPa = 0, tP = 0, tPC = 0, tMin = 0, tMax = 0;
        HashMap<String, List<Stats>> grouped = new HashMap<String, List<Stats>>();

        for (Stats s : lstStats) {
            if (grouped.containsKey(s.shift.name())) {
                grouped.get(s.shift.name()).add(s);
            } else {
                List<Stats> list = new ArrayList<Stats>(0);
                list.add(s);
                grouped.put(s.shift.name(), list);
            }
            tPa += s.total_passengers;
            tP += s.total_money_earn;
            tPC += s.total_money_company;
            tMin += s.min_price;
            tMax += s.max_price;
            System.out.println(System.out.format("| %10s | %15s | %15s | %15s | %20s | %20s | %20s |",
                    s.name, s.total_passengers, twoDecimal(s.total_money_earn), twoDecimal(s.total_money_company), s.min_price, s.max_price, s.shift.name()));
        }

        System.out.println();
        System.out.println();
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Indicator", "Agent", "Week", "Day"));
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Calls", twoDecimal(tPa / 12), twoDecimal(tPa), twoDecimal(tPa / 7)));
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Profit", twoDecimal(tP / 12), twoDecimal(tP), twoDecimal(tP / 7)));
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Company Profit", twoDecimal(tPC / 12), twoDecimal(tPC), twoDecimal(tPC / 7)));
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Min Bid", twoDecimal(tMin / 12), twoDecimal(tMin), twoDecimal(tMin / 7)));
        System.out.println(System.out.format("| %15s | %15s | %15s | %15s |", "Min Bid", twoDecimal(tMax / 12), twoDecimal(tMax), twoDecimal(tMax / 7)));

    }

    public String twoDecimal(double value) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(value);
    }

    private void sentRequest() {

        switch (activity) {
            case WAITING_FOR_CALLS:
                lstStats.clear();
                // Send the cfp to all sellers
                System.out.println("(" + agent.runtime.toString() + ")  Sending request to all agents");
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < agent.lstTaxi.size(); ++i) {
                    cfp.addReceiver(agent.lstTaxi.get(i));
                }
                try {
                    cfp.setContentObject(agent.lastRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cfp.setConversationId("auction");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                agent.send(cfp);
                // Prepare the template to get proposals
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                activity = Activity.WAITING_FOR_BIDS;
                break;
            case WAITING_FOR_BIDS:
                // Receive all proposals/refusals from seller agents
                ACLMessage reply = agent.receive(mt);
                Request response = null;
                if (reply != null) {

                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(reply.getByteSequenceContent());
                        ObjectInput in;
                        try {
                            in = new ObjectInputStream(bis);
                            response = ((Request) in.readObject());
                            lstStats.add(response.stats);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("(" + agent.runtime.toString() + ")  Reply from " + reply.getSender().getLocalName() + " : " + (response != null ? response.bid.payOff : 0) + " NT");
                        // This is an offer

                        assert response != null;
                        response.bidder = reply.getSender();
                        biddingList.add(response);
                    } else {
                      ByteArrayInputStream bis = new ByteArrayInputStream(reply.getByteSequenceContent());
                      ObjectInput in = null;
                      try {
                          in = new ObjectInputStream(bis);
                          response = ((Request) in.readObject());
                          lstStats.add(response.stats);
                      } catch (IOException | ClassNotFoundException e) {
                          e.printStackTrace();
                      }
                    }
                    repliesCnt++;
                    if (repliesCnt >= agent.lstTaxi.size()) {
                        processBids();
                        // We received all replies
                        activity = Activity.PROCESSING_BIDS;
                    }
                } else {
                    block();
                }
                break;
            case PROCESSING_BIDS:
                //
                try {
                    Thread.sleep(5);
                } catch (Exception ignored) {
                }
                // Sending confirmation to taxi for best offer
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestTaxi);
                try {
                    order.setContentObject(lastBestRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                order.setConversationId("auction");
                order.setReplyWith("call" + System.currentTimeMillis());
                agent.send(order);
                // Prepare the template to get the purchase order reply
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                activity = Activity.WAITING_TAXI_CONFIRMATION;
                //SEND MESSAGE TO CONFIRM BID ACCEPTED
                break;
            case WAITING_TAXI_CONFIRMATION:
                // RESPONSE OF TAXI WITH JOB ALLOCATED
                ACLMessage confirmation = agent.receive(mt);
                if (confirmation != null) {
                    switch (confirmation.getPerformative()) {
                        case ACLMessage.CONFIRM:
                            nextCall();
                            repliesCnt = 0;
                            bestPrice = 0;
                            bestTaxi = null;
                            activity = Activity.WAITING_FOR_CALLS;
                            System.out.println("(" + agent.runtime.toString() + ")  ");
                            break;
                        case ACLMessage.DISCONFIRM:
                            System.out.println("Error allocation job");

                    }
                } else {
                    block();
                }
                break;
        }
    }

    private void processBids() {
        double first, second;
        first = second = Integer.MAX_VALUE;
        for (Request r : biddingList) {
            if (r.bid.payOff < first) {
                second = first;
                first = r.bid.payOff;
                bestTaxi = r.bidder;
                lastBestRequest = r;
            } else if (r.bid.payOff < second && r.bid.payOff != first)
                second = r.bid.payOff;
        }
        lastBestRequest.bid.company -= second;
        lastBestRequest.bid.payOff -= lastBestRequest.bid.company;
        bestPrice = lastBestRequest.bid.payOff;
    }
}
