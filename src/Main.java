import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        new Main().run(10000000);
    }

    private enum Country {
        Spain,
        England,
        Germany,
        Italy,
        Austria,
        France
    }

    private int mExceptionCase = 0;
    private ArrayList<Team> mFirstTeams = new ArrayList<Team>();
    private ArrayList<Team> mSecondTeams = new ArrayList<Team>();

    private void run(int count) {
        initTeamList(count);

        for (int i=0; i<count; i++) {
            //System.out.println("Run " + i);
            if (!tournament()) {
                i--;
            }
        }

        printPercentage(count);
    }

    private void initTeamList(int count) {
        mFirstTeams.add(new Team(0, "PSG", Country.France, 'A', count));
        mSecondTeams.add(new Team(1, "Real Madrid", Country.Spain, 'A', count));
        mFirstTeams.add(new Team(2, "Bayern", Country.Germany, 'B', count));
        mSecondTeams.add(new Team(3, "Tottenham", Country.England, 'B', count));
        mFirstTeams.add(new Team(4, "Man. City", Country.England, 'C', count));
        mSecondTeams.add(new Team(5, "Atalanta", Country.Italy, 'C', count));
        mFirstTeams.add(new Team(6, "Juventus", Country.Italy, 'D', count));
        mSecondTeams.add(new Team(7, "Atletico Madrid", Country.Spain, 'D', count));
        mFirstTeams.add(new Team(8, "Liverpool", Country.England, 'E', count));
        mSecondTeams.add(new Team(9, "Napoli", Country.Italy, 'E', count));
        mFirstTeams.add(new Team(10, "Barcelona", Country.Spain, 'F', count));
        mSecondTeams.add(new Team(11, "Dortmund", Country.Germany, 'F', count));
        mFirstTeams.add(new Team(12, "RB Liepzig", Country.Austria, 'G', count));
        mSecondTeams.add(new Team(13, "Lyon", Country.France, 'G', count));
        mFirstTeams.add(new Team(14, "Valencia", Country.Spain, 'H', count));
        mSecondTeams.add(new Team(15, "Chelsea", Country.England, 'H', count));
    }

    private boolean tournament() {
        for (Team team : mFirstTeams) {
            team.setPicked(false);
        }
        for (Team team : mSecondTeams) {
            team.setPicked(false);
        }

        int[] history = new int[16];
        boolean isSuccess = true;

        for (int i=0; i<8; i++) {
            List<Team> secondTeam = mSecondTeams.stream().filter(t -> !t.isPicked()).collect(Collectors.toList());
            Team team = secondTeam.get(getRandom(secondTeam.size()));
            int teamId = team.getId();

            List<Team> targetTeams = mFirstTeams.stream()
                    .filter(targetTeam -> (!targetTeam.isPicked()) &&
                            (targetTeam.getCountry() != team.getCountry()) &&
                            (targetTeam.getGroup() != team.getGroup()))
                    .collect(Collectors.toList());
            if (targetTeams.size() == 0) {
                mExceptionCase++;
                isSuccess = false;
                break;
            }
            Team target = targetTeams.get(getRandom(targetTeams.size()));
            int targetId = target.getId();

            //System.out.println(team.getName() + " picked " + target.getName());

            team.setPicked(true);
            history[teamId] = targetId;
//            (team.getPickedCounter())[targetId] += 1;

            target.setPicked(true);
            history[targetId] = teamId;
//            (target.getPickedCounter())[teamId] += 1;
        }

        if (isSuccess) {
            for (Team team : mFirstTeams) {
                (team.getPickedCounter())[history[team.getId()]]++;
            }
            for (Team team : mSecondTeams) {
                (team.getPickedCounter())[history[team.getId()]]++;
            }
        }

        return isSuccess;
    }

    private int getRandom(int max) {
        return (int)(Math.random() * max);
    }

    private void printPercentage(int count) {
        for (Team team : mFirstTeams) {
            System.out.println(team);
            System.out.println("==============================================");
        }
        for (Team team : mSecondTeams) {
            System.out.println(team);
            System.out.println("==============================================");
        }
        System.out.println("Exception case: " + ((float)mExceptionCase / (float)(count / 100)));
    }

    private class Team {
        private int mId;
        private boolean mIsPicked = false;
        private String mName;
        private Country mCountry;
        private char mGroup;
        private int [] mPickedCounter = new int[16];
        private int mCount;

        Team(int id, String name, Country country, char group, int count) {
            mId = id;
            mName = name;
            mCountry = country;
            mGroup = group;
            mCount = count;

            for (int i=0; i<16; i++) {
                mPickedCounter[i] = 0;
            }
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public boolean isPicked() {
            return mIsPicked;
        }

        public void setPicked(boolean picked) {
            mIsPicked = picked;
        }

        public Country getCountry() {
            return mCountry;
        }

        public void setCountry(Country country) {
            mCountry = country;
        }

        public char getGroup() {
            return mGroup;
        }

        public void setGroup(char group) {
            mGroup = group;
        }

        public int[] getPickedCounter() {
            return mPickedCounter;
        }

        public void setPickedCounter(int[] pickedCounter) {
            mPickedCounter = pickedCounter;
        }

        public void setCount(int count) {
            mCount = count;
        }

        public String getName() {
            return mName;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder("mId=" + mId + ", mName=" + mName + "\n");
            int total = 0;
            for (int count : mPickedCounter) {
                total += count;
            }
            if (total == mCount) {
                for (int id = 0; id < 16; id++) {
                    if (mPickedCounter[id] > 0) {
                        List<Team> pickedTeams;
                        final int finalId = id;
                        pickedTeams = mSecondTeams.stream().filter(team -> team.getId() == finalId).collect(Collectors.toList());
                        pickedTeams.addAll(mFirstTeams.stream().filter(team -> team.getId() == finalId).collect(Collectors.toList()));
                        Team targetTeam = pickedTeams.get(0);
                        if (targetTeam != null) {
                            s.append("Name: ").append(targetTeam.mName).append(", Percentage: ").append((float) mPickedCounter[id] / (float) (mCount / 100) + "\n");
                        }
                    }
                }
            } else {
                s.append("Count is not valid.\n");
            }
            return s.toString();
        }
    }
}
