const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read reviews", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("reviews").get());
});

test("Authenticated user: can create a valid game review", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("reviews").doc("review_1").set({
      id: "review_1",
      userId: ALICE_UID,
      authorName: "Alice Gamer",
      gameId: "elden_ring",
      gameTitle: "Elden Ring",
      gameTitleAr: "ألدن رينج",
      rating: 5,
      status: "COMPLETED",
      hoursPlayed: 120,
      reviewTitle: "Masterpiece",
      reviewBody: "Amazing boss fights and rich lore.",
      platformPlayed: "PS5",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create review for another user", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("reviews").doc("review_fake").set({
      id: "review_fake",
      userId: ALICE_UID, // spoofing Alice
      gameId: "elden_ring",
      gameTitle: "Elden Ring",
      rating: 5,
      status: "COMPLETED",
      createdAt: new Date(),
    })
  );
});

test("Community user: can read reviews and add a comment", async () => {
  // Seed Alice's review as admin
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("reviews").doc("review_1").set({
      id: "review_1",
      userId: ALICE_UID,
      gameId: "elden_ring",
      gameTitle: "Elden Ring",
      rating: 5,
      status: "COMPLETED",
      likesCount: 0,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  // Bob can read
  await assertSucceeds(bobDb.collection("reviews").doc("review_1").get());

  // Bob cannot modify Alice's review content
  await assertFails(
    bobDb.collection("reviews").doc("review_1").update({
      reviewTitle: "Hacked by Bob",
      updatedAt: new Date(),
    })
  );

  // Bob can like the review
  await assertSucceeds(
    bobDb.collection("reviews").doc("review_1").update({
      likesCount: 1,
    })
  );

  // Bob can comment on the review
  await assertSucceeds(
    bobDb.collection("reviews").doc("review_1").collection("comments").doc("comment_1").set({
      userId: BOB_UID,
      authorName: "Bob Gamer",
      commentText: "Totally agree with your review!",
      createdAt: new Date(),
    })
  );
});
